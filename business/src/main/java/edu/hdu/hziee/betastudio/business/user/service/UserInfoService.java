package edu.hdu.hziee.betastudio.business.user.service;

import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.business.user.request.UserRequest;

public interface UserInfoService {

    AppUserInfoBO getAppInfo(Long userId);

    void updateUserName(UserRequest request);

    void updatePic(UserRequest request);
}
