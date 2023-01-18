package edu.hdu.hziee.betastudio.business.lesson.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

@Data
@Builder
@ToString
@ApiModel(value = "学生选课信息导入模型")
public class UserLessonExcelBO {

    @ExcelProperty({"姓名"})
    @ApiModelProperty(name = "用户姓名")
    private String realName;

    @ExcelProperty({"学号"})
    @ApiModelProperty(name = "用户学号")
    private String stuId;

    @ExcelProperty({"年级"})
    @ApiModelProperty(name = "用户年级")
    private String grade;

    @ExcelProperty({"专业"})
    @ApiModelProperty(name = "用户专业")
    private String major;

    @ExcelProperty({"班级"})
    @ApiModelProperty(name = "用户班级")
    private String clazz;

    @Tolerate
    public UserLessonExcelBO(){}
}
