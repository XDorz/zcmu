package edu.hdu.hziee.betastudio.business.comment.model;

import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@ApiModel(value = "主题模型")
public class ThemeBO {

    @ApiModelProperty(name = "主题id")
    private Long themeId;

    @ApiModelProperty(name = "主题名称")
    private String themeName;

    @ApiModelProperty(name = "发布人id")
    private Long userId;

    @ApiModelProperty(name = "主题发布人信息")
    private AppUserInfoBO appUserInfoBO;

    @ApiModelProperty(name = "帖子内容")
    private CommentBO comment;

    @ApiModelProperty(name = "评论链")
    private List<CommentBO> commentList;

    //一评论10热度，一浏览1热度
    @ApiModelProperty(name = "主题热度")
    private int hot;

    //一订阅量8热度
    @ApiModelProperty(name = "帖子关注度(订阅数量)")
    private int subscribed;

    //评论量
    @ApiModelProperty(name = "主题评论量")
    private int commentNum;
}
