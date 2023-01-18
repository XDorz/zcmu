package edu.hdu.hziee.betastudio.business.user.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Date;

@Data
@Builder
@ToString
@ApiModel(value = "用户登录模型")
public class UserBO {

    @ApiModelProperty(name = "用户id")
    private Long userId;

    @ApiModelProperty(name = "用户账户")
    private String account;

    @ApiModelProperty(name = "用户密码")
    private String password;

//    @ApiModelProperty(name = "盐")
//    private String salt;

    @ApiModelProperty(name = "用户最后登录时间")
    private Date lastLoginDate;

    @ApiModelProperty(name = "用户登录ip")
    private String lastLoginIp;
}
