package edu.hdu.hziee.betastudio.business.user.request;

import edu.hdu.hziee.betastudio.business.aop.PermRequest;
import edu.hdu.hziee.betastudio.business.user.model.UserInfoBO;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
public class UserRequest extends PermRequest {

    private Long userId;

    /**
     * 目标id，用于管理员赋予权限等
     */
    private Long targetUserId;

    private String account;

    private String password;

    private String lastLoginIp;

    private String userName;

    private String picUrl;

    /**
     * 图片
     */
    private MultipartFile picFile;

    private MultipartFile userExcelFile;

}
