package edu.hdu.hziee.betastudio.web.controller;

import edu.hdu.hziee.betastudio.business.resours.model.ResoursBO;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateCallBack;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateTemplate;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import edu.hdu.hziee.betastudio.web.aop.CheckLogin;
import edu.hdu.hziee.betastudio.web.request.ResourceRestRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Slf4j
@RestController
@RequestMapping("/resource")
public class ResourceController {

    @Autowired
    ResoursService resoursService;

    @CheckLogin
    @PostMapping("/upload")
    public ZCMUResult<ResoursBO> uploadResource(ResourceRestRequest request, HttpServletRequest httpServletRequest
            , @PathParam("resourceFile") MultipartFile resourceFile, @PathParam("picFile") MultipartFile picFile){
        return OperateTemplate.operate(log, "用户上传资源", request, httpServletRequest, new OperateCallBack<ResoursBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(resourceFile,ExceptionResultCode.ILLEGAL_PARAMETERS,"上传的资源无法为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<ResoursBO> operate(){
                ResoursRequest resoursRequest = ResoursRequest.builder()
                        .belongId(request.getBelongId())
                        .picFile(picFile)
                        .info(request.getInfo())
                        .name(StringUtils.hasText(request.getName())?request.getName():resourceFile.getOriginalFilename())
                        .sourceFile(resourceFile)
                        .userId(request.getUserId())
                        .build();
                return RestUtil.buildSuccessResult(resoursService.createResource(resoursRequest),"上传成功");
            }
        });
    }

    /**
     * 比起直接请求该接口，更加推荐在其他接口内部就将资源与对象关联
     * 因为请求该接口前须得等对象创建玩返回其id值
     * 所以【该接口仅为备用】
     * {@link #uploadResource(ResourceRestRequest, HttpServletRequest, MultipartFile, MultipartFile)}
     */
    @CheckLogin
    @PutMapping("/connect")
    public ZCMUResult<Void> connectResource(ResourceRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "关联资源", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getResourceIds(),ExceptionResultCode.ILLEGAL_PARAMETERS,"关联的资源id无法为空");
                AssertUtil.assertNotNull(request.getBelongId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"关联的对象id无法为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                ResoursRequest resoursRequest = ResoursRequest.builder()
                        .belongId(request.getBelongId())
                        .userId(request.getUserId())
                        .resourceList(request.getResourceIds())
                        .build();
                resoursService.connectResource(resoursRequest);
                return RestUtil.buildSuccessResult(null,"资源关联成功");
            }
        });
    }

    @CheckLogin
    @DeleteMapping
    public ZCMUResult<Void> deleteResource(ResourceRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "删除资源", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getResourceId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"资源id无法为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                ResoursRequest resoursRequest = ResoursRequest.builder()
                        .resourceId(request.getResourceId())
                        .userId(request.getUserId())
                        .build();
                resoursRequest.setVerifyId(request.getUserId());
                resoursService.deleteResource(resoursRequest);
                return RestUtil.buildSuccessResult(null,"资源删除成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/name")
    public ZCMUResult<Void> updateResourceName(ResourceRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "修改资源名称", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getResourceId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"资源id无法为空");
                AssertUtil.assertNotNull(request.getName(),ExceptionResultCode.ILLEGAL_PARAMETERS,"新的资源名称无法为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                ResoursRequest resoursRequest = ResoursRequest.builder()
                        .resourceId(request.getResourceId())
                        .userId(request.getUserId())
                        .name(request.getName())
                        .build();
                resoursRequest.setVerifyId(request.getUserId());
                resoursService.updateResourceName(resoursRequest);
                return RestUtil.buildSuccessResult(null,"资源名称修改成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/info")
    public ZCMUResult<Void> updateResourceInfo(ResourceRestRequest request, HttpServletRequest httpServletRequest){
        return OperateTemplate.operate(log, "修改资源名称", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getResourceId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"资源id无法为空");
                AssertUtil.assertNotNull(request.getInfo(),ExceptionResultCode.ILLEGAL_PARAMETERS,"新的信息无法为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                ResoursRequest resoursRequest = ResoursRequest.builder()
                        .resourceId(request.getResourceId())
                        .userId(request.getUserId())
                        .info(request.getInfo())
                        .build();
                resoursRequest.setVerifyId(request.getUserId());
                resoursService.updateResourceInfo(resoursRequest);
                return RestUtil.buildSuccessResult(null,"资源信息修改成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/pic")
    public ZCMUResult<Void> updateResourcePic(ResourceRestRequest request, HttpServletRequest httpServletRequest, @PathParam("picFile") MultipartFile picFile){
        return OperateTemplate.operate(log, "更新资源封面图", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest,ExceptionResultCode.ILLEGAL_PARAMETERS,"请求不能为空");
                AssertUtil.assertNotNull(request.getResourceId(),ExceptionResultCode.ILLEGAL_PARAMETERS,"资源id无法为空");
                AssertUtil.assertNotNull(picFile,ExceptionResultCode.ILLEGAL_PARAMETERS,"封面图片不能为空");
                AssertUtil.assertNotNull(request.getUserId(),ExceptionResultCode.UNAUTHORIZED,"用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() {
                ResoursRequest resoursRequest = ResoursRequest.builder()
                        .resourceId(request.getResourceId())
                        .picFile(picFile)
                        .userId(request.getUserId())
                        .build();
                resoursRequest.setVerifyId(request.getUserId());
                resoursService.updateResourcePicUrl(resoursRequest);
                return RestUtil.buildSuccessResult(null,"资源封面替换成功");
            }
        });
    }
}
