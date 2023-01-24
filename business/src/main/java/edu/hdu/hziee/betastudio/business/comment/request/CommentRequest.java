package edu.hdu.hziee.betastudio.business.comment.request;

import edu.hdu.hziee.betastudio.business.aop.PermRequest;
import edu.hdu.hziee.betastudio.business.comment.model.CommentBO;
import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CommentRequest extends PermRequest {

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

    //评论所属ip地址
    private String ipAddr;

    //客户端类型
    private String clientType;

    //主题标题
    private String themeTitle;

    //增加的评论量
    private int countNum;

    //热度
    private int hot;

    //评论所带图片ids
    private List<Long> picIdList;
}
