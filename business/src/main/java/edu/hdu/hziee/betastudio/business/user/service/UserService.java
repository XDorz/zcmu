package edu.hdu.hziee.betastudio.business.user.service;


import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.business.user.model.UserInfoBO;
import edu.hdu.hziee.betastudio.business.user.request.UserRequest;

import java.util.List;

public interface UserService {

    /**
     * 用户注册，带多个权限信息
     */
    //todo 在后面加上给予权限类型
    String register(String account, String password, String realName,long stuId);

    /**
     * 批量注册，优化账号检查
     */
    //todo 为批量设定好权限信息
    String register(UserRequest request);

    /**
     * 用户登录
     */
    String login(UserRequest request);

    /**
     * 用户登录态检查
     */
    boolean verifyLogin(String token);

    void logout(Long userId);

    Integer updatePassword(UserRequest request);
}
