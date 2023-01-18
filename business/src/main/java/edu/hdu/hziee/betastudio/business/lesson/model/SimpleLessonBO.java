package edu.hdu.hziee.betastudio.business.lesson.model;

import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@ApiModel(value = "简易课程模型模型")
public class SimpleLessonBO {

    @ApiModelProperty(name = "课程id")
    private Long lessonId;

    @ApiModelProperty(name = "课程创建人Id")
    private Long userId;

    @ApiModelProperty(name = "课程创建人")
    private AppUserInfoBO creater;

    @ApiModelProperty(name = "课程名")
    private String lessonName;

    @ApiModelProperty(name = "课程封面")
    private String picUrl;

    @ApiModelProperty(name = "课程介绍")
    private String info;
}
