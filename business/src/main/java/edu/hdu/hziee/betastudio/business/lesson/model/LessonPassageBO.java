package edu.hdu.hziee.betastudio.business.lesson.model;

import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@Builder
@ToString
@ApiModel(value = "课程章节模型")
public class LessonPassageBO {

    @ApiModelProperty(name = "章节id")
    private Long passageId;

    @ApiModelProperty(name = "所属课程id")
    private Long lessonId;

    @ApiModelProperty(name = "章节名称")
    private String name;

    @ApiModelProperty(name = "资源列表")
    List<ResoursBO> resoursBOList;
}
