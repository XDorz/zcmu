package edu.hdu.hziee.betastudio.business.comment.convert;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.comment.model.SimpleThemeBO;
import edu.hdu.hziee.betastudio.business.comment.model.ThemeBO;
import edu.hdu.hziee.betastudio.business.comment.service.CommentService;
import edu.hdu.hziee.betastudio.business.user.service.UserInfoService;
import edu.hdu.hziee.betastudio.dao.comment.model.ThemeDO;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ThemeConvert {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    CommentService commentService;

    public ThemeBO convert(ThemeDO themeDO){
        AssertUtil.assertNotNull(themeDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return ThemeBO.builder()
                .themeId(themeDO.getThemeId())
                .appUserInfoBO(userInfoService.getAppInfo(themeDO.getUserId()))
                .hot(themeDO.getHot())
                .commentNum(themeDO.getCommentNum())
                .comment(commentService.findComment(themeDO.getThemeId()))
                .commentList(commentService.findCommentList(themeDO.getThemeId()))
                .userId(themeDO.getUserId())
                .themeId(themeDO.getThemeId())
                .build();
    }

    public ThemeDO convert(ThemeBO themeBO){
        AssertUtil.assertNotNull(themeBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return ThemeDO.builder()
                .themeId(themeBO.getThemeId()==null?IdUtil.getSnowflakeNextId():themeBO.getThemeId())
                .userId(themeBO.getUserId())
                .hot(themeBO.getHot())
                .commentNum(themeBO.getCommentNum())
                .deleted(false)
                .build();
    }

    public SimpleThemeBO convertSimple(ThemeDO themeDO){
        AssertUtil.assertNotNull(themeDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return SimpleThemeBO.builder()
                .appUserInfoBO(userInfoService.getAppInfo(themeDO.getUserId()))
                .commentNum(themeDO.getCommentNum())
                .hot(themeDO.getHot())
                .themeId(themeDO.getThemeId())
                .comment(commentService.findComment(themeDO.getThemeId()))
                .build();
    }
}
