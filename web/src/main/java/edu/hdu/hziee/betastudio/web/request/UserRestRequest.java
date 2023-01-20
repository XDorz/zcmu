package edu.hdu.hziee.betastudio.web.request;

import edu.hdu.hziee.betastudio.web.aop.UserCheckedRequest;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class UserRestRequest implements UserCheckedRequest {

    private Long userId;

    /**
     * 目标id，用于管理员赋予权限等
     */
    private Long targetUserId;

    private String account;

    private String password;

    private String userName;

    private String picUrl;

    private List<Long> userIds;

}
