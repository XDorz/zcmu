package edu.hdu.hziee.betastudio.business.comment.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.comment.convert.ThemeConvert;
import edu.hdu.hziee.betastudio.business.comment.model.SimpleThemeBO;
import edu.hdu.hziee.betastudio.business.comment.model.ThemeBO;
import edu.hdu.hziee.betastudio.business.comment.request.CommentRequest;
import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.dao.comment.model.ThemeDO;
import edu.hdu.hziee.betastudio.dao.comment.repo.ThemeDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.OperateLevelEnum;
import edu.hdu.hziee.betastudio.util.customenum.PermEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class ThemeServiceImpl implements ThemeService{

    @Autowired
    CommentService commentService;

    @Autowired
    ThemeConvert convert;

    @Autowired
    ThemeDORepo themeDORepo;

    @Autowired
    ResoursService resoursService;

    @Autowired
    PermService permService;

    @Override
    public ThemeBO getTheme(CommentRequest request) {
        ThemeDO themeDO = themeDORepo.findAllByThemeId(request.getThemeId());
        //convert中做过判空操作，所以此处不必担心热度增加会找不到对应的主题帖
        ThemeBO themeBO = convert.convert(themeDO);

        //增加浏览量热度
        request.setSkipVerify(true);
        request.setHot(1);
        increaseCountNum(request);
        return themeBO;
    }

    @Override
    public List<SimpleThemeBO> getAllTheme(CommentRequest request) {
        return CollectionUtils.toStream(themeDORepo.findAllByDeleted(false))
                .filter(Objects::nonNull)
                .map(convert::convertSimple)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ThemeBO createTheme(CommentRequest request) {
        long themeId = IdUtil.getSnowflakeNextId();
        ThemeDO themeDO = ThemeDO.builder()
                .themeId(themeId)
                .themeName(request.getThemeTitle())
                .userId(request.getUserId())
                .hot(0)
                .commentNum(0)
                .deleted(false)
                .build();
        themeDORepo.save(themeDO);

        //为主题帖创建内容
        CommentRequest commentRequest = CommentRequest.builder()
                .commentId(themeId)
                .ipAddr(request.getIpAddr())
                .clientType(request.getClientType())
                .content(request.getContent())
                .userId(request.getUserId())
                .themeId(null)
                .masterId(null)
                .previousCommentId(null)
                .build();
        commentRequest.setSkipVerify(true);
        commentService.createComment(commentRequest);

        //为主题关联图片
        ResoursRequest resoursRequest = ResoursRequest.builder()
                .resourceList(request.getPicIdList())
                .belongId(themeId)
                .build();
        resoursRequest.setSkipVerify(true);
        resoursService.connectResource(resoursRequest);
        //因为调用不会太频繁，故此处使用资源开销大的convert进行转换
        return convert.convert(themeDO);
    }

    /**
     * 防止混乱需要使用同步锁
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void increaseCountNum(CommentRequest request) {
        themeDORepo.increaseCommentNum(request.getThemeId(),request.getCountNum());
    }

    /**
     * 防止混乱需要使用同步锁
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public synchronized void increaseHot(CommentRequest request) {
        themeDORepo.increaseHot(request.getThemeId(),request.getHot());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteTheme(CommentRequest request) {
        AssertUtil.assertTrue(verifyBelong(request.getUserId(),request.getThemeId()).hasPerm(OperateLevelEnum.MEDIUM_OPERATE)
                , ExceptionResultCode.FORBIDDEN,"您无权删除该帖子！");
        themeDORepo.deleteTheme(request.getThemeId(),true);
    }

    private OperateLevelEnum verifyBelong(Long userId, Long themeId) {
        if (userId == null || themeId == null) {
            return OperateLevelEnum.FORBIDDEN;
        }
        ThemeDO themeDO = themeDORepo.findAllByThemeId(themeId);
        if (themeDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        //帖子发布者可以操作自己的评论
        if (userId.equals(themeDO.getUserId())) {
            return OperateLevelEnum.TOTAL_OPERATE;
        }

        //总管理可以操作帖子
        UserPermRequest userPermRequest = UserPermRequest.builder()
                .userId(userId)
                .codeName(PermEnum.MANAGER.getCode())
                .build();
        userPermRequest.setSkipVerify(true);
        if(permService.userExistPerm(userPermRequest)){
            return OperateLevelEnum.MEDIUM_OPERATE;
        }
        return OperateLevelEnum.FORBIDDEN;
    }
}
