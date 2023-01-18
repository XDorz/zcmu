package edu.hdu.hziee.betastudio.web.request;

import edu.hdu.hziee.betastudio.web.aop.UserCheckedRequest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class ResourceRestRequest implements UserCheckedRequest {

    private Long resourceId;

    //资源所属id
    private Long belongId;

    private Long userId;

    //资源url
    private String url;

    //资源描述
    private String info;

    //资源名称
    private String name;

    //资源封面
    private String picUrl;

    //图片文件
    private MultipartFile picFile;

    //批量关联
    private List<Long> resourceIds;
}
