package edu.hdu.hziee.betastudio.business.perm.service;


import edu.hdu.hziee.betastudio.business.perm.model.PermBO;
import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;

import java.util.List;

public interface PermService {

    /**
     * 验证用户是否有对应权限
     * 【内部接口，不对外提供】
     */
    boolean userExistPerm(UserPermRequest request);

    PermBO createPerm(UserPermRequest request);

    PermBO deletePerm(UserPermRequest request);

    void givePerm(UserPermRequest request);

    void takeBackPerm(UserPermRequest request);

    List<PermBO> getAllPerm(UserPermRequest request);

    List<List<PermBO>> getAllUserPermInfo(UserPermRequest request);

    List<PermBO> getUserPermInfo(UserPermRequest request);
}
