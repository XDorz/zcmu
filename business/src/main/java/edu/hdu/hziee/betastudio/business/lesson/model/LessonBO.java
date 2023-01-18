package edu.hdu.hziee.betastudio.business.lesson.model;

import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString
@ApiModel(value = "课程模型")
public class LessonBO {

    @ApiModelProperty(name = "课程id")
    private Long lessonId;

    @ApiModelProperty(name = "课程创建人Id")
    private Long userId;

    @ApiModelProperty(name = "课程创建人")
    private AppUserInfoBO creater;

    @ApiModelProperty(name = "课程所属章节")
    private List<LessonPassageBO> lessonPassageBOList;

    @ApiModelProperty(name = "课程作业")
    private List<HomeworkBO> homeworkBOList;

    @ApiModelProperty(name = "资源列表")
    List<ResoursBO> resoursBOList;

    @ApiModelProperty(name = "课程名")
    private String lessonName;

    @ApiModelProperty(name = "课程封面")
    private String picUrl;

    @ApiModelProperty(name = "课程介绍")
    private String info;

    @ApiModelProperty(name = "额外信息")
    private Map<String,String> ext;
}
