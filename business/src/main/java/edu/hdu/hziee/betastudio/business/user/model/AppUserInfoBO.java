package edu.hdu.hziee.betastudio.business.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@Builder
@ToString
@ApiModel(value = "用户信息显示模型")
public class AppUserInfoBO {

    @ApiModelProperty(name = "用户id")
    private Long userId;

    @ApiModelProperty(name = "学号")
    private Long stuId;

    @ApiModelProperty(name = "用户昵称")
    private String userName;

    @ApiModelProperty(name = "用户头像")
    private String picUrl;

    @ApiModelProperty(name = "其他信息")
    private Map<String,String> ext;

    //todo 在此处添加权限信息，亦或者是教师/学生区分
}
