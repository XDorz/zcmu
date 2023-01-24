package edu.hdu.hziee.betastudio.business.comment.model;

import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@ApiModel(value = "评论区模型")
public class CommentBO {
    @ApiModelProperty(name = "评论id")
    private Long commentId;

    @ApiModelProperty(name = "评论属主题id(帖子id)")
    private Long themeId;

    @ApiModelProperty(name = "评论人id")
    private Long userId;

    @ApiModelProperty(name = "评论人信息")
    private AppUserInfoBO userInfo;

    @ApiModelProperty(name = "前一个评论的id")
    private Long previousCommentId;

    @ApiModelProperty(name = "主评论id")
    private Long masterId;

    @ApiModelProperty(name = "评论链(仅masterComment有)")
    private List<CommentBO> commentBOList;

    @ApiModelProperty(name = "评论内容")
    private String content;

    @ApiModelProperty(name = "评论所属ip地址")
    private String ipAddr;

    @ApiModelProperty(name = "客户端类型")
    private String client_type;

    @ApiModelProperty(name = "评论附带的图片(考虑到性能目前只支持主题使用)")
    private List<ResoursBO> picResource;

    @ApiModelProperty(name = "是否是教师评论")
    private boolean teacher;
}
