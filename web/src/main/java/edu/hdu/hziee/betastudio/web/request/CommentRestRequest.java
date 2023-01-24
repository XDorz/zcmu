package edu.hdu.hziee.betastudio.web.request;

import edu.hdu.hziee.betastudio.web.aop.UserCheckedRequest;
import lombok.Data;

import java.util.List;

@Data
public class CommentRestRequest implements UserCheckedRequest {

    //评论id
    private Long commentId;

    //评论属主题id(帖子id)
    private Long themeId;

    //用户id
    private Long userId;

    //前一个评论的id
    private Long previousCommentId;

    //主评论id
    private Long masterId;

    //评论内容
    private String content;

    //客户端类型
    private String clientType;

    //主题标题
    private String themeTitle;

    //增加的热度
    private int hot;

    //评论所带图片ids
    private List<Long> picIdList;
}
