package edu.hdu.hziee.betastudio.business.resours.model;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import jakarta.persistence.Column;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;

import java.util.Map;

@Data
@Builder
@ToString
@ApiModel(value = "资源模型")
public class ResoursBO {

    @ApiModelProperty(name = "资源id")
    private Long resourceId;

    @ApiModelProperty(name = "所属(关联)id")
    private Long belongId;

    @ApiModelProperty(name = "上传者id")
    private Long userId;

    @ApiModelProperty(name = "资源url")
    private String url;

    @ApiModelProperty(name = "资源名称")
    private String name;

    @ApiModelProperty(name = "资源描述")
    private String info;

    @ApiModelProperty(name = "资源图片(课程用)")
    private String picUrl;

    @ApiModelProperty(name = "资源额外信息")
    private Map<String,String> ext;
}
