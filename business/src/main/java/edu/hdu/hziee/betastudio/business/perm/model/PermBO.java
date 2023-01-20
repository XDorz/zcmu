package edu.hdu.hziee.betastudio.business.perm.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

@Data
@Builder
@ToString
@ApiModel(value = "权限模型")
public class PermBO {

    @ApiModelProperty(name = "权限id")
    private Long permId;

    @ApiModelProperty(name = "权限名称")
    private String permName;

    @ApiModelProperty(name = "权限code")
    private String codeName;

    @ApiModelProperty(name = "是否拥有此权限")
    private boolean havePerm;
}
