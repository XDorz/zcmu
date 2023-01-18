package edu.hdu.hziee.betastudio.business.lesson.model;

import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.util.customenum.HomeworkStatusEnum;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
@Builder
@ToString
@ApiModel(value = "课程作业模型")
public class HomeworkBO {

    @ApiModelProperty(name = "作业id")
    private Long homeworkId;

    @ApiModelProperty(name = "所属课程id")
    private Long lessonId;

    @ApiModelProperty(name = "课程名称")
    private String lessonName;
    @ApiModelProperty(name = "创建者名称")
    private String creatorName;

    @ApiModelProperty(name = "作业要求")
    private String info;

    @ApiModelProperty(name = "作业名称")
    private String name;

    @ApiModelProperty(name = "作业起始时间")
    private Date start;

    @ApiModelProperty(name = "作业结束时间")
    private Date end;

    /**
     * 推荐使用以下方法转换而非通过convert转换
     * 通过convert方法可能会在日后版本中造成某些不可预估的依赖循环
     * {@link edu.hdu.hziee.betastudio.business.lesson.service.HomeworkServiceImpl#getHomeworkStatus(Long, Long)}
     */
    @ApiModelProperty(name = "作业状态,【convert时需传入userId】")
    private HomeworkStatusEnum status;

    @ApiModelProperty(name = "自己提交的作业,【convert时需传入userId】")
    private SubmitHomeworkBO subHomework;

    @ApiModelProperty(name = "作业额外信息")
    private Map<String,String> ext;

    @ApiModelProperty(name = "资源列表")
    List<ResoursBO> resoursBOList;
}
