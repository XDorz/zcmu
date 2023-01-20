package edu.hdu.hziee.betastudio.business.perm.request;

import edu.hdu.hziee.betastudio.business.aop.PermRequest;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class UserPermRequest extends PermRequest {

    //权限id
    private Long permId;

    //用户id
    private Long userId;

    //操作目标用户id
    private Long targetUserId;

    //权限名称
    private String permName;

    //权限code
    private String codeName;

    //权限-用户 关系id
    private Long relationId;

    //操作的权限id链表
    private List<Long> permIds;
}
