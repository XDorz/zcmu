package edu.hdu.hziee.betastudio.business.user.model;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;

import java.util.Map;

@Data
@Builder
@ToString
@ApiModel(value = "用户信息模型")
public class UserInfoBO {

    @ExcelIgnore
    @ApiModelProperty(name = "用户id")
    private Long userId;

    //todo 为他附上工号的别称
    @ExcelProperty({"学号"})
    @ApiModelProperty(name = "学号")
    private Long stuId;

    @ExcelProperty({"工号"})
    @ApiModelProperty(name = "工号，导入教师表时用，其余时间无用")
    private Long workId;

    @ExcelProperty({"电子邮箱"})
    @ApiModelProperty(name = "电子邮箱")
    private String email;

    @ExcelProperty({"姓名"})
    @ApiModelProperty(name = "真实姓名")
    private String realName;

    @ExcelProperty({"性别"})
    @ApiModelProperty(name = "性别")
    private String sex;

    @ExcelProperty({"学院"})
    @ApiModelProperty(name = "学院名")
    private String collage;


    @ExcelProperty({"年级"})
    @ApiModelProperty(name = "年级")
    private Integer grade;

    @ExcelProperty({"专业"})
    @ApiModelProperty(name = "专业")
    private String major;

    @ExcelProperty({"班级"})
    @ApiModelProperty(name = "班级")
    private String userClass;

    @ExcelProperty({"学生标记"})
    @ApiModelProperty(name = "学生标记")
    private String stuMark;

    @ExcelProperty({"辅修标记"})
    @ApiModelProperty(name = "辅修标记")
    private String minorMark;

    @ExcelProperty({"是否重修"})
    @ApiModelProperty(name = "是否重修")
    private boolean repair;

    @ExcelProperty({"是否补修"})
    @ApiModelProperty(name = "是否补修")
    private boolean patch;

    @ExcelProperty({"是否自修"})
    @ApiModelProperty(name = "是否自修")
    private boolean selfStudy;

    @ExcelIgnore
    @ApiModelProperty(name = "用户昵称")
    private String userName;

    @ExcelIgnore
    @ApiModelProperty(name = "用户头像")
    private String picUrl;

    @ExcelIgnore
    @ApiModelProperty(name = "其他信息")
    private Map<String,String> ext;

    @ExcelIgnore
    @ApiModelProperty(name = "是否已经删除")
    private boolean deleted;

    @Tolerate
    public UserInfoBO(){}
}

