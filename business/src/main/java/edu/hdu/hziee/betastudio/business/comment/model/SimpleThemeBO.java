package edu.hdu.hziee.betastudio.business.comment.model;

import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@ApiModel(value = "简易主题模型")
public class SimpleThemeBO {

    @ApiModelProperty(name = "主题id")
    private Long themeId;

    @ApiModelProperty(name = "主题名称")
    private String themeName;

    @ApiModelProperty(name = "主题发布人信息")
    private AppUserInfoBO appUserInfoBO;

    @ApiModelProperty(name = "帖子内容")
    private CommentBO comment;

    //一评论10热度，一浏览1热度
    @Column(name="hot",updatable = true,nullable = false,unique = false,columnDefinition = "int comment '热度'")
    private int hot;

    //一订阅量8热度
    @ApiModelProperty(name = "帖子关注度(订阅数量)")
    private int subscribed;

    //评论量
    @Column(name="comment_num",updatable = true,nullable = false,unique = false,columnDefinition = "int comment '评论量'")
    private int commentNum;
}
