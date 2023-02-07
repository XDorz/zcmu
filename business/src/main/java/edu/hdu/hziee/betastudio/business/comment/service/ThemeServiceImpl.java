package edu.hdu.hziee.betastudio.business.comment.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.comment.convert.ThemeConvert;
import edu.hdu.hziee.betastudio.business.comment.model.SimpleThemeBO;
import edu.hdu.hziee.betastudio.business.comment.model.ThemeBO;
import edu.hdu.hziee.betastudio.business.comment.request.CommentRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.business.perm.verify.VerifyOperate;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.dao.comment.model.ThemeDO;
import edu.hdu.hziee.betastudio.dao.comment.model.ThemeSubscribeDO;
import edu.hdu.hziee.betastudio.dao.comment.repo.ThemeDORepo;
import edu.hdu.hziee.betastudio.dao.comment.repo.ThemeSubscribeDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.OperateLevelEnum;
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

    @Autowired
    ThemeSubscribeDORepo themeSubscribeDORepo;

    @Autowired
    private void setVerify(VerifyOperate verifyOperate){
        this.verifyOperate=verifyOperate.getInstance(this::customVerify);
    }
    private VerifyOperate verifyOperate;

    /**
     * 同一类中的方法不受SpringAOP的代理，故此处increaseHot中的 @Transactional 注解无效
     * 该方法需要补一个 @Transactional 来完成修改操作
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ThemeBO getTheme(CommentRequest request) {
        ThemeDO themeDO = themeDORepo.findAllByThemeId(request.getThemeId());
        //convert中做过判空操作，所以此处不必担心热度增加会找不到对应的主题帖
        ThemeBO themeBO = convert.convert(themeDO);

        //增加浏览量热度
        request.setSkipVerify(true);
        request.setHot(1);
        increaseHot(request);
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

    @Override
    public List<SimpleThemeBO> getAllSelfTheme(CommentRequest request) {
        return CollectionUtils.toStream(themeDORepo.findAllByUserIdAndDeleted(request.getUserId(),false))
                .filter(Objects::nonNull)
                .map(convert::convertSimple)
                .toList();
    }

    @Override
    public void subscribeTheme(CommentRequest request) {
        AssertUtil.assertTrue(!themeSubscribeDORepo.existsByUserIdAndThemeId(request.getUserId(), request.getThemeId())
                ,ExceptionResultCode.ILLEGAL_PARAMETERS,"无法重复订阅主题！");
        ThemeDO themeDO = themeDORepo.findAllByThemeId(request.getThemeId());
        AssertUtil.assertNotNull(themeDO,ExceptionResultCode.FORBIDDEN,"该主题不存在，无法订阅！");
        AssertUtil.assertTrue(!themeDO.isDeleted(),ExceptionResultCode.FORBIDDEN,"该主题已被删除，无法订阅！");


        long subscribeId = IdUtil.getSnowflakeNextId();
        ThemeSubscribeDO subscribeDO = ThemeSubscribeDO.builder()
                .subscribeId(subscribeId)
                .userId(request.getUserId())
                .themeId(request.getThemeId())
                .build();
        //todo 如果有推送模块，则推送该消息
        themeSubscribeDORepo.save(subscribeDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unSubscribeTheme(CommentRequest request) {
        themeSubscribeDORepo.deleteByUserIdAndThemeId(request.getUserId(),request.getThemeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteAllThemeSubscribe(Long themeId) {
        return themeSubscribeDORepo.deleteAllByThemeId(themeId);
    }

    @Override
    public List<SimpleThemeBO> getSubscribeThemes(CommentRequest request) {
        return CollectionUtils.toStream(themeSubscribeDORepo.findByUserId(request.getUserId()))
                .filter(Objects::nonNull)
                .map(ThemeSubscribeDO::getThemeId)
                .map(themeDORepo::findAllByThemeId)
                .filter(Objects::nonNull)
                .map(convert::convertSimple)
                .toList();
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
        ThemeDO themeDO = themeDORepo.findAllByThemeId(request.getThemeId());
        AssertUtil.assertNotNull(themeDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"查无该主题帖");
        AssertUtil.assertTrue(!themeDO.isDeleted(),ExceptionResultCode.ILLEGAL_PARAMETERS,"无法删除已经删除的主题帖");
        AssertUtil.assertTrue(verifyOperate.verifyLevel(request.getVerifyId(),request.getThemeId()).hasPerm(OperateLevelEnum.MEDIUM_OPERATE)
                , ExceptionResultCode.FORBIDDEN,"您无权删除该帖子！");
        themeDORepo.deleteTheme(request.getThemeId(),true);
        //删除该帖子的所有订阅( !无法撤销！)
        deleteAllThemeSubscribe(themeDO.getThemeId());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateThemeName(CommentRequest request) {
        AssertUtil.assertTrue(verifyOperate.verifyLevel(request.getVerifyId(),request.getThemeId()).hasPerm(OperateLevelEnum.MEDIUM_OPERATE)
                , ExceptionResultCode.FORBIDDEN,"您无权修改该帖子的标题！");

        themeDORepo.updateThemeName(request.getThemeId(),request.getThemeTitle());
    }


    //====================================以下为自定义鉴权与归属鉴定方法========================================

    private OperateLevelEnum customVerify(Long userId,Long themeId){
        ThemeDO themeDO = themeDORepo.findAllByThemeId(themeId);
        if (themeDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        //帖子发布者可以操作自己的评论
        if (userId.equals(themeDO.getUserId())) {
            return OperateLevelEnum.TOTAL_OPERATE;
        }
        return null;
    }
}
