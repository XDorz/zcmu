package edu.hdu.hziee.betastudio.web.controller;

import edu.hdu.hziee.betastudio.business.perm.model.PermBO;
import edu.hdu.hziee.betastudio.business.perm.request.UserPermRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateCallBack;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateTemplate;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import edu.hdu.hziee.betastudio.web.aop.CheckLogin;
import edu.hdu.hziee.betastudio.web.request.PermRestRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/perm")
public class PermController {

    @Autowired
    PermService permService;

    /**
     * 【备用接口！该接口目前无用】
     */
    @CheckLogin
    @PostMapping("/create")
    public ZCMUResult<PermBO> createPerm(PermRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户创建权限", request, httpServletRequest, new OperateCallBack<PermBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getPermName(),ExceptionResultCode.ILLEGAL_PARAMETERS,"权限名称不能为空");
                AssertUtil.assertNotNull(request.getCodeName(),ExceptionResultCode.ILLEGAL_PARAMETERS,"权限code不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<PermBO> operate() {
                UserPermRequest userPermRequest = UserPermRequest.builder()
                        .permName(request.getPermName())
                        .codeName(request.getCodeName())
                        .build();
                userPermRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(permService.createPerm(userPermRequest),"权限创建成功");
            }
        });
    }

    @CheckLogin
    @DeleteMapping
    public ZCMUResult<PermBO> deletePerm(PermRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "用户删除权限", request, httpServletRequest, new OperateCallBack<PermBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getPermId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"权限id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<PermBO> operate() {
                UserPermRequest userPermRequest = UserPermRequest.builder()
                        .permId(request.getPermId())
                        .build();
                userPermRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(permService.deletePerm(userPermRequest),"权限删除成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/give")
    public ZCMUResult<Void> givePerm(PermRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "给予权限", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getPermId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"权限id不能为空");
                AssertUtil.assertNotNull(request.getTargetUserId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"目标用户id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                UserPermRequest userPermRequest = UserPermRequest.builder()
                        .userId(request.getTargetUserId())
                        .permId(request.getPermId())
                        .build();
                userPermRequest.setVerifyId(request.getUserId());
                permService.givePerm(userPermRequest);
                return RestUtil.buildSuccessResult(null,"给予权限成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/takeback")
    public ZCMUResult<PermBO> takeBackPerm(PermRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "收回用户权限", request, httpServletRequest, new OperateCallBack<PermBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getPermIds(),ExceptionResultCode.ILLEGAL_PARAMETERS,"权限id列表不能为空");
                AssertUtil.assertNotNull(request.getTargetUserId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"目标用户id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<PermBO> operate() throws IOException {
                UserPermRequest userPermRequest = UserPermRequest.builder()
                        .userId(request.getTargetUserId())
                        .permIds(request.getPermIds())
                        .build();
                userPermRequest.setVerifyId(request.getUserId());
                permService.takeBackPerm(userPermRequest);
                return RestUtil.buildSuccessResult(null,"收回权限成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/all")
    public ZCMUResult<List<PermBO>> getAllPerm(PermRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "查看所有权限", request, httpServletRequest, new OperateCallBack<List<PermBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<List<PermBO>> operate() {
                UserPermRequest userPermRequest = UserPermRequest.builder()
                        .build();
                userPermRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(permService.getAllPerm(userPermRequest),"获取所有权限成功！");
            }
        });
    }

    @CheckLogin
    @PostMapping("/user/perm")
    public ZCMUResult<List<List<PermBO>>> userPerm(PermRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "查看某用户的所有权限情况", request, httpServletRequest, new OperateCallBack<List<List<PermBO>>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getTargetUserId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"目标用户id不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<List<List<PermBO>>> operate() throws IOException {
                UserPermRequest userPermRequest = UserPermRequest.builder()
                        .userId(request.getTargetUserId())
                        .build();
                userPermRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(permService.getAllUserPermInfo(userPermRequest),"获取用户权限信息成功");
            }
        });
    }

    @CheckLogin
    @GetMapping
    public ZCMUResult<List<PermBO>> userHadPerm(PermRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "查看某用户拥有的权限", request, httpServletRequest, new OperateCallBack<List<PermBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<List<PermBO>> operate() throws IOException {
                UserPermRequest userPermRequest = UserPermRequest.builder()
                        .userId(request.getUserId())
                        .build();
                userPermRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(permService.getUserPermInfo(userPermRequest),"获取用户权限信息成功");
            }
        });
    }

}
