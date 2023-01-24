package edu.hdu.hziee.betastudio.business.comment.convert;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.comment.model.CommentBO;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.business.user.service.UserInfoService;
import edu.hdu.hziee.betastudio.dao.comment.model.CommentDO;
import edu.hdu.hziee.betastudio.dao.comment.repo.CommentDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Component
public class CommentConvert {

    @Autowired
    ResoursService resoursService;

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    CommentDORepo commentDORepo;
    public CommentBO convert(CommentDO commentDO){
        AssertUtil.assertNotNull(commentDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        CommentBO commentBO = CommentBO.builder()
                .previousCommentId(commentDO.getPreviousCommentId())
                .client_type(commentDO.getClient_type())
                .ipAddr(commentDO.getIpAddr())
                .themeId(commentDO.getThemeId())
                .commentId(commentDO.getCommentId())
                .content(commentDO.getContent())
                .teacher(commentDO.isTeacher())
                .masterId(commentDO.getMasterId())
                .userInfo(userInfoService.getAppInfo(commentDO.getUserId()))
                .userId(commentDO.getUserId())
                .build();

        //为主评论构建评论链
        if(commentDO.getMasterId()==null&&commentDO.getThemeId()!=null){
            List<CommentDO> commentDOList = commentDORepo.findAllByMasterIdAndDeleted(commentBO.getCommentId(), false);
            List<CommentBO> commentBOList = CollectionUtils.toStream(commentDOList)
                    .filter(Objects::nonNull)
                    .map(this::convert)
                    .toList();
            commentBO.setCommentBOList(commentBOList);
        }
        //为主题内容赋予图片列表
        if(commentDO.getThemeId()==null){
            commentBO.setPicResource(resoursService.getListByBelongId(commentDO.getCommentId()));
        }
        return commentBO;
    }

    public CommentDO convert(CommentBO commentBO){
        return CommentDO.builder()
                .commentId(commentBO.getCommentId()==null? IdUtil.getSnowflakeNextId(): commentBO.getCommentId())
                .content(commentBO.getContent())
                .previousCommentId(commentBO.getPreviousCommentId())
                .teacher(commentBO.isTeacher())
                .themeId(commentBO.getThemeId())
                .client_type(commentBO.getClient_type())
                .ipAddr(commentBO.getIpAddr())
                .userId(commentBO.getUserId())
                .masterId(commentBO.getMasterId())
                .deleted(false)
                .build();
    }
}
