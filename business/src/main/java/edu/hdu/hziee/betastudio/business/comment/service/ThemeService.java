package edu.hdu.hziee.betastudio.business.comment.service;

import edu.hdu.hziee.betastudio.business.comment.model.SimpleThemeBO;
import edu.hdu.hziee.betastudio.business.comment.model.ThemeBO;
import edu.hdu.hziee.betastudio.business.comment.request.CommentRequest;
import edu.hdu.hziee.betastudio.dao.comment.model.ThemeDO;

import java.util.List;

public interface ThemeService {

    ThemeBO getTheme(CommentRequest request);

    List<SimpleThemeBO> getAllTheme(CommentRequest request);

    ThemeBO createTheme(CommentRequest request);

    List<SimpleThemeBO> getAllSelfTheme(CommentRequest request);

    void subscribeTheme(CommentRequest request);

    List<SimpleThemeBO> getSubscribeThemes(CommentRequest request);

    void unSubscribeTheme(CommentRequest request);

    /**
     * 【内部接口，不对外开放】
     */
    Integer deleteAllThemeSubscribe(Long themeId);

    void increaseCountNum(CommentRequest request);

    void increaseHot(CommentRequest request);

    void deleteTheme(CommentRequest request);

    void updateThemeName(CommentRequest request);
}
