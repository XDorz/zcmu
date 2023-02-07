package edu.hdu.hziee.betastudio.business.user.service;

import edu.hdu.hziee.betastudio.business.user.convert.UserConvert;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.business.user.request.UserRequest;
import edu.hdu.hziee.betastudio.dao.user.repo.UserInfoDORepo;
import edu.hdu.hziee.betastudio.util.common.ZCMUException;
import edu.hdu.hziee.betastudio.util.tecentcos.CosUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

@Slf4j
@Service
public class UserInfoSeriveImpl implements UserInfoService{

    @Autowired
    UserConvert convert;

    @Autowired
    UserInfoDORepo userInfoDORepo;

    @Autowired
    CosUtil cosUtil;

    @Override
    public AppUserInfoBO getAppInfo(Long userId) {
        return convert.convertApp(userInfoDORepo.findAllByUserId(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateUserName(UserRequest request) {
        userInfoDORepo.updateUserName(request.getUserId(),request.getUserName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePic(UserRequest request) {
        MultipartFile picFile = request.getPicFile();
        InputStream picStream = null;
        try {
            picStream = picFile.getInputStream();
        } catch (IOException e) {
            log.error("无法从图片文件中获取输入流！",e);
            throw new ZCMUException("图片保存失败");
        }
        String picUrl = cosUtil.uploadFile(picFile.getOriginalFilename(), picStream);
        userInfoDORepo.updatePic(request.getUserId(),picUrl);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updatePicByByte(UserRequest request) {
        ByteArrayInputStream bin=new ByteArrayInputStream( request.getPicByte());
        String picUrl = cosUtil.uploadFile("png", bin);
        userInfoDORepo.updatePic(request.getUserId(),picUrl);
    }
}
