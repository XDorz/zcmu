package edu.hdu.hziee.betastudio.web.controller;

import cn.hutool.core.codec.Base62Codec;
import cn.hutool.core.codec.Base64Decoder;
import edu.hdu.hziee.betastudio.business.user.request.UserRequest;
import edu.hdu.hziee.betastudio.business.user.service.UserInfoService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateCallBack;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateTemplate;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import edu.hdu.hziee.betastudio.util.tecentcos.CosUtil;
import edu.hdu.hziee.betastudio.web.aop.CheckLogin;
import edu.hdu.hziee.betastudio.web.request.UserRestRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

@Slf4j
@RestController
@RequestMapping("/userinfo")
public class UserInfoController {

    @Autowired
    UserInfoService userInfoService;

    @CheckLogin
    @PutMapping("/username")
    public ZCMUResult<Void> changeUserName(UserRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户修改昵称", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserName(), ExceptionResultCode.ILLEGAL_PARAMETERS,"修改的昵称不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED,"用户id不能为空");
            }

            @Override
            public ZCMUResult<Void> operate() throws Exception {
                UserRequest userRequest=UserRequest.builder()
                        .userId(request.getUserId())
                        .userName(request.getUserName())
                        .build();
                userInfoService.updateUserName(userRequest);
                return RestUtil.buildSuccessResult("修改昵称成功");
            }
        });
    }

    //todo 有机会改为put
    @CheckLogin
    @PostMapping("/pic")
    public ZCMUResult<Void> changePic(UserRestRequest request, HttpServletRequest httpServletRequest
            , @PathParam("picFile") MultipartFile picFile){
        return OperateTemplate.operate(log, "用户修改头像", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(picFile, ExceptionResultCode.ILLEGAL_PARAMETERS,"新的头像不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED,"用户id不能为空");
            }

            @Override
            public ZCMUResult<Void> operate() throws Exception {
                UserRequest userRequest=UserRequest.builder()
                        .userId(request.getUserId())
                        .picFile(picFile)
                        .build();
                userInfoService.updatePic(userRequest);
                return RestUtil.buildSuccessResult("修改头像成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/pic")
    public ZCMUResult<Void> changePic(UserRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户修改头像", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getPic(), ExceptionResultCode.ILLEGAL_PARAMETERS,"新的头像不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED,"用户id不能为空");
            }

            @Override
            public ZCMUResult<Void> operate() throws Exception {
                byte[] decode = Base64Decoder.decode(request.getPic().getBytes());

                UserRequest userRequest=UserRequest.builder()
                        .userId(request.getUserId())
                        .picByte(decode)
                        .build();
                userInfoService.updatePicByByte(userRequest);
                return RestUtil.buildSuccessResult("修改头像成功");
            }
        });
    }
}
