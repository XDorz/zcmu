package edu.hdu.hziee.betastudio.business.comment.service;

import edu.hdu.hziee.betastudio.business.comment.model.CommentBO;
import edu.hdu.hziee.betastudio.business.comment.request.CommentRequest;

import java.util.List;

public interface CommentService {

    /**
     * 【内部接口，不对外开放！】
     */
    CommentBO findComment(Long commentId);

    CommentBO findComment(CommentRequest request);

    /**
     *  获取主题下的所有评论
     * 【内部接口，不对外开放！】
     * 【返回的列表中不包含commentId所对应的评论】
     */
    List<CommentBO> findCommentList(Long themeId);

    List<CommentBO> findCommentList(CommentRequest request);

    CommentBO createComment(CommentRequest request);

    /**
     * 删除评论
     * 该操作无法撤销
     */
    void deleteComment(CommentRequest request);

    void updateContent(CommentRequest request);

}
