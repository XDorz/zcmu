package edu.hdu.hziee.betastudio.business.comment.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.comment.convert.CommentConvert;
import edu.hdu.hziee.betastudio.business.comment.model.CommentBO;
import edu.hdu.hziee.betastudio.business.comment.request.CommentRequest;
import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.business.perm.verify.VerifyOperate;
import edu.hdu.hziee.betastudio.dao.comment.model.CommentDO;
import edu.hdu.hziee.betastudio.dao.comment.model.ThemeDO;
import edu.hdu.hziee.betastudio.dao.comment.repo.CommentDORepo;
import edu.hdu.hziee.betastudio.dao.comment.repo.ThemeDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ClientTypeEnum;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.OperateLevelEnum;
import edu.hdu.hziee.betastudio.util.customenum.PermEnum;
import edu.hdu.hziee.betastudio.util.customenum.basic.EnumUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private ThemeDORepo themeDORepo;

    @Autowired
    private CommentConvert convert;

    @Autowired
    private CommentDORepo commentDORepo;

    @Autowired
    private PermService permService;

    @Lazy
    @Autowired
    private ThemeService themeService;

    @Autowired
    private void setVerify(VerifyOperate verifyOperate){
        this.verifyOperate=verifyOperate.getInstance(this::customVerify);
    }
    private VerifyOperate verifyOperate;

    @Override
    public CommentBO findComment(Long commentId) {
        return convert.convert(commentDORepo.findAllByCommentId(commentId));
    }

    @Override
    public CommentBO findComment(CommentRequest request) {
        return findComment(request.getCommentId());
    }

    @Override
    public List<CommentBO> findCommentList(Long themeId) {
        List<CommentDO> commentDOList = commentDORepo.findAllByThemeIdAndMasterIdAndDeleted(themeId, null,false);
        return CollectionUtils.toStream(commentDOList)
                .filter(Objects::nonNull)
                .map(convert::convert)
                .toList();
    }

    @Override
    public List<CommentBO> findCommentList(CommentRequest request) {
        ThemeDO themeDO = themeDORepo.findAllByThemeId(request.getThemeId());
        AssertUtil.assertTrue(!themeDO.isDeleted(),ExceptionResultCode.FORBIDDEN,"该主题帖已被删除！");
        return findCommentList(request.getThemeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommentBO createComment(CommentRequest request) {
        //主题帖内容创建需要设置commentId为主题id
        long commentId = request.getCommentId() == null ? IdUtil.getSnowflakeNextId() : request.getCommentId();
        AssertUtil.assertTrue(EnumUtil.isExist(ClientTypeEnum.class, request.getClientType())
                , ExceptionResultCode.ILLEGAL_PARAMETERS, "非法的客户端！");
        CommentDO commentDO = CommentDO.builder()
                .commentId(commentId)
                .masterId(request.getMasterId())
                .previousCommentId(request.getPreviousCommentId())
                .userId(request.getUserId())
                .ipAddr(request.getIpAddr())
                .client_type(request.getClientType())
                .themeId(request.getThemeId())
                .content(request.getContent())
                .deleted(false)
                .build();

        UserPermRequest userPermRequest = UserPermRequest.builder()
                .userId(request.getUserId())
                .codeName(PermEnum.TEACHER.getCode())
                .build();
        userPermRequest.setSkipVerify(true);
        commentDO.setTeacher(permService.userExistPerm(userPermRequest));
        commentDORepo.save(commentDO);

        //增加热度与评论量
        if (request.getThemeId() != null) {
            request.setSkipVerify(true);
            request.setCountNum(1);
            themeService.increaseCountNum(request);
            request.setHot(10);
            themeService.increaseHot(request);
        }
        return convert.convert(commentDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteComment(CommentRequest request) {
        CommentDO commentDO = commentDORepo.findAllByCommentId(request.getCommentId());
        AssertUtil.assertNotNull(commentDO, ExceptionResultCode.ILLEGAL_PARAMETERS, "查无该评论！");
        AssertUtil.assertTrue(verifyOperate.verifyLevel(request.getVerifyId(),request.getCommentId()).hasPerm(OperateLevelEnum.MEDIUM_OPERATE)
                ,ExceptionResultCode.FORBIDDEN,"您无权删除该评论！");
        AssertUtil.assertTrue(!commentDO.isDeleted(), ExceptionResultCode.ILLEGAL_PARAMETERS, "无法重复删除评论！");

        //执行删除步骤
        //1.将评论改为删除状态
        commentDORepo.deleteComment(commentDO.getCommentId(), true);
        //2.如果有后续评论则将后续评论的previousId改为其前一个的
        CommentDO nextComment = commentDORepo.findAllByPreviousCommentIdAndDeleted(commentDO.getCommentId(), false);
        if (nextComment != null) {
            commentDORepo.updatePreviousId(nextComment.getCommentId(), commentDO.getPreviousCommentId());
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateContent(CommentRequest request) {
        AssertUtil.assertTrue(verifyOperate.verifyLevel(request.getVerifyId(),request.getCommentId()).hasPerm(OperateLevelEnum.TOTAL_OPERATE)
                ,ExceptionResultCode.FORBIDDEN,"您无权修改该评论！");
        commentDORepo.updateContent(request.getCommentId(), request.getContent());
    }



    //====================================以下为自定义鉴权与归属鉴定方法========================================

    private OperateLevelEnum customVerify(Long userId,Long commentId){
        CommentDO commentDO = commentDORepo.findAllByCommentId(commentId);
        if (commentDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        //评论发布者可以操作自己的评论
        if (userId.equals(commentDO.getUserId())) {
            return OperateLevelEnum.TOTAL_OPERATE;
        }

        //帖子发布人可以操作评论
        Long themeId=commentDO.getThemeId()==null?commentDO.getCommentId():commentDO.getThemeId();
        ThemeDO themeDO = themeDORepo.findAllByThemeId(themeId);
        if(userId.equals(themeDO.getUserId())){
            return OperateLevelEnum.MEDIUM_OPERATE;
        }
        return null;
    }

}
