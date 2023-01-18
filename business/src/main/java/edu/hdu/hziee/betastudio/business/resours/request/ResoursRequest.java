package edu.hdu.hziee.betastudio.business.resours.request;

import edu.hdu.hziee.betastudio.business.aop.PermRequest;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
@ToString
public class ResoursRequest extends PermRequest {

    private Long resourceId;

    //资源所属id
    private Long belongId;

    private Long userId;

    //资源url
    private String url;

    //资源名称
    private String name;

    //资源描述
    private String info;

    //资源封面
    private String picUrl;

    //图片文件
    private MultipartFile picFile;

    //资源文件
    private MultipartFile sourceFile;

    //多个资源id关联同一个belongId
    private List<Long> resourceList;

}
