package edu.hdu.hziee.betastudio.business.lesson.model;

import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
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
@ApiModel(value = "已提交作业模型")
public class SubmitHomeworkBO {

    @ApiModelProperty(name = "作业提交id")
    private Long submitId;

    @ApiModelProperty(name = "关联的作业id")
    private Long homeworkId;

    @ApiModelProperty(name = "提交者id")
    private Long userId;

    @ApiModelProperty(name = "提交者信息")
    private AppUserInfoBO user;

    @ApiModelProperty(name = "提交内容")
    private String content;

    @ApiModelProperty(name = "教师打分")
    private Integer score;

    @ApiModelProperty(name = "资源列表")
    List<ResoursBO> resoursBOList;
}
