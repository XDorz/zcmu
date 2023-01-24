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

    void increaseCountNum(CommentRequest request);

    void increaseHot(CommentRequest request);

    void deleteTheme(CommentRequest request);
}
