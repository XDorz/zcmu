package edu.hdu.hziee.betastudio.web.controller;

import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.business.user.request.UserRequest;
import edu.hdu.hziee.betastudio.business.user.service.UserInfoService;
import edu.hdu.hziee.betastudio.business.user.service.UserService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.IpUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateCallBack;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateTemplate;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import edu.hdu.hziee.betastudio.web.aop.CheckLogin;
import edu.hdu.hziee.betastudio.web.request.UserRestRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    UserService userService;

    @Autowired
    UserInfoService userInfoService;

    //todo 待系统自检并创建管理账号完成后恢复用户登录检查，目前测试暂不检查用户登录
    @CheckLogin
    @PostMapping("/batchregister")
    public ZCMUResult<String> register(UserRestRequest request, HttpServletRequest httpServletRequest
            , @PathParam("userExcel") MultipartFile userExcel){
        return OperateTemplate.operate(log, "excel学生批量注册", request, httpServletRequest, new OperateCallBack<String>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(userExcel,ExceptionResultCode.ILLEGAL_PARAMETERS,"请传入用户名册");
            }

            @Override
            public ZCMUResult<String> operate() throws IOException {
                UserRequest userRequest = UserRequest.builder()
                        .userExcelFile(userExcel)
                        .build();
                userRequest.setVerifyId(request.getUserId());
                String result = userService.register(userRequest);
                if(result==null){
                    return RestUtil.buildSuccessResult("本次批量注册成功","未发现错误");
                }else {
                    return RestUtil.buildFailResult("批量注册失败，本次没有账号被注册\n"+result);
                }
            }
        });
    }


    @PostMapping("/login")
    public ZCMUResult<String> login(UserRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户普通登录", request, httpServletRequest, new OperateCallBack<String>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getAccount(), ExceptionResultCode.ILLEGAL_PARAMETERS,"账户不能为空");
                AssertUtil.assertNotNull(request.getPassword(), ExceptionResultCode.ILLEGAL_PARAMETERS,"密码不能为空");
            }

            @Override
            public ZCMUResult<String> operate() {
                UserRequest userRequest=UserRequest.builder()
                        .account(request.getAccount())
                        .password(request.getPassword())
                        .lastLoginIp(IpUtil.getIp(httpServletRequest))
                        .build();
                String token = userService.login(userRequest);
                return RestUtil.buildSuccessResult(token,"登录成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/session")
    public ZCMUResult<AppUserInfoBO> session(UserRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户登录维持", request, httpServletRequest, new OperateCallBack<AppUserInfoBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED,"用户id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<AppUserInfoBO> operate() {
                return RestUtil.buildSuccessResult(userInfoService.getAppInfo(request.getUserId()));
            }
        });
    }

    @CheckLogin
    @GetMapping("logout")
    public ZCMUResult<Void> logout(UserRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户登出", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() throws Exception {
                userService.logout(request.getUserId());
                return RestUtil.buildSuccessResult("登出成功");
            }
        });
    }

    @CheckLogin
    @PostMapping("/pwd")
    public ZCMUResult<Void> changePWD(UserRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户修改密码", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getPassword(), ExceptionResultCode.ILLEGAL_PARAMETERS,"新密码不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() throws Exception {
                UserRequest userRequest=UserRequest.builder()
                        .userId(request.getUserId())
                        .password(request.getPassword())
                        .build();
                userService.updatePassword(userRequest);
                return RestUtil.buildSuccessResult("修改密码成功");
            }
        });
    }

    @CheckLogin
    @DeleteMapping
    public ZCMUResult<Void> deleteUser(UserRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "删除用户", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserIds(), ExceptionResultCode.ILLEGAL_PARAMETERS,"删除用户列表不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() throws Exception {
                UserRequest userRequest=UserRequest.builder()
                        .build();
                userRequest.setVerifyId(request.getUserId());
                for (Long userId : request.getUserIds()) {
                    userRequest.setUserId(userId);
                    userService.deleteUser(userRequest);
                }
                return RestUtil.buildSuccessResult("用户批量删除成功");
            }
        });
    }
}
