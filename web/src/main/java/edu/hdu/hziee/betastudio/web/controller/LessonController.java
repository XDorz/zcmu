package edu.hdu.hziee.betastudio.web.controller;

import edu.hdu.hziee.betastudio.business.lesson.model.LessonBO;
import edu.hdu.hziee.betastudio.business.lesson.model.LessonPassageBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SimpleLessonBO;
import edu.hdu.hziee.betastudio.business.lesson.request.LessonRequest;
import edu.hdu.hziee.betastudio.business.lesson.service.LessonService;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateCallBack;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateTemplate;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import edu.hdu.hziee.betastudio.web.aop.CheckLogin;
import edu.hdu.hziee.betastudio.web.request.LessonRestRequest;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.websocket.server.PathParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/lesson")
public class LessonController {

    @Autowired
    LessonService lessonService;

    @CheckLogin
    @PostMapping("/create")
    public ZCMUResult<LessonBO> createLesson(LessonRestRequest request, HttpServletRequest httpServletRequest
            , @PathParam("picFile") MultipartFile picFile) {
        return OperateTemplate.operate(log, "教师创建课程", request, httpServletRequest, new OperateCallBack<LessonBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getName(), ExceptionResultCode.ILLEGAL_PARAMETERS, "课程名不能为空");
                AssertUtil.assertNotNull(request.getInfo(), ExceptionResultCode.ILLEGAL_PARAMETERS, "课程介绍不能为空");
            }

            @Override
            public ZCMUResult<LessonBO> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .name(request.getName())
                        .info(request.getInfo())
                        .picFile(picFile)
                        .userId(request.getUserId())
                        .resourceList(request.getResourceList())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(lessonService.createLesson(lessonRequest), "课程创建成功");
            }
        });
    }

    /**
     * 获取简易课程列表，用于小程序总览显示
     * 要想获取详细课程请用lessonId查询
     * {@link LessonController#getLesson(LessonRestRequest, HttpServletRequest)}
     */
    @CheckLogin
    @GetMapping("/simple")
    public ZCMUResult<List<SimpleLessonBO>> getSimpleLesson(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "学生查询自己的课程", request, httpServletRequest, new OperateCallBack<List<SimpleLessonBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<List<SimpleLessonBO>> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .userId(request.getUserId())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(lessonService.getAllChooseLesson(lessonRequest), "获取课程简略信息成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/teacher/created/simple")
    public ZCMUResult<List<SimpleLessonBO>> getCreatedLesson(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "教师查询自己创建的课程", request, httpServletRequest, new OperateCallBack<List<SimpleLessonBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<List<SimpleLessonBO>> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .userId(request.getUserId())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(lessonService.getAllCreateLesson(lessonRequest), "查询创建的课程成功");
            }
        });
    }

    @CheckLogin
    @GetMapping
    public ZCMUResult<LessonBO> getLesson(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "查询课程的详细信息", request, httpServletRequest, new OperateCallBack<LessonBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "查询的课程id不能为空");
            }

            @Override
            public ZCMUResult<LessonBO> operate() {
                LessonRequest lessonRequest = LessonRequest.builder()
                        .userId(request.getUserId())
                        .lessonId(request.getLessonId())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(lessonService.getLessonById(lessonRequest), "获取详细课程信息成功");
            }
        });
    }

    @CheckLogin
    @PostMapping("/create/passage")
    public ZCMUResult<LessonPassageBO> createLessonPassage(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "教师创建课程章节", request, httpServletRequest, new OperateCallBack<LessonPassageBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getName(), ExceptionResultCode.ILLEGAL_PARAMETERS, "课程章节名不能为空");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "所属课程id不能为空");
            }

            @Override
            public ZCMUResult<LessonPassageBO> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .name(request.getName())
                        .userId(request.getUserId())
                        .lessonId(request.getLessonId())
                        .resourceList(request.getResourceList())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(lessonService.createPassageLesson(lessonRequest), "课程章节创建成功");
            }
        });
    }

    @CheckLogin
    @PostMapping("/importuser")
    public ZCMUResult<Void> createLessonPassage(LessonRestRequest request, HttpServletRequest httpServletRequest
            , @PathParam("userExcelFile")MultipartFile userExcelFile) {
        return OperateTemplate.operate(log, "教师导入选课名单", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");;
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "导入课程的id不能为空");
                AssertUtil.assertNotNull(userExcelFile, ExceptionResultCode.ILLEGAL_PARAMETERS, "导入的选课名单不能为空");
            }

            @Override
            public ZCMUResult<Void> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .userId(request.getUserId())
                        .lessonId(request.getLessonId())
                        .userExcelFile(userExcelFile)
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                lessonService.batchImportStudent(lessonRequest);
                return RestUtil.buildSuccessResult(null, "选课名单导入成功");
            }
        });
    }

    /**
     * 该内容以包含在LessonBO中，该接口仅作为备用
     * {@link LessonController#getLesson(LessonRestRequest, HttpServletRequest)}
     */
    @CheckLogin
    @GetMapping("/passage")
    public ZCMUResult<LessonPassageBO> getPassage(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "查看章节详情", request, httpServletRequest, new OperateCallBack<LessonPassageBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getPassageId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "查询的章节id不能为空");
            }

            @Override
            public ZCMUResult<LessonPassageBO> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .passageId(request.getPassageId())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(lessonService.getPassageById(lessonRequest), "章节详情查询成功");
            }
        });
    }

    /**
     * 该内容以包含在LessonBO中，该接口仅作为备用
     * {@link LessonController#getLesson(LessonRestRequest, HttpServletRequest)}
     */
    @CheckLogin
    @GetMapping("/lesson/passage")
    public ZCMUResult<List<LessonPassageBO>> getLessonPassage(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "查看课程的章节详情", request, httpServletRequest, new OperateCallBack<List<LessonPassageBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "查询的课程id不能为空");
            }

            @Override
            public ZCMUResult<List<LessonPassageBO>> operate() {
                LessonRequest lessonRequest = LessonRequest.builder()
                        .lessonId(request.getLessonId())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(lessonService.getPassageByLessonId(lessonRequest), "获取课程的所有章节信息成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/name")
    public ZCMUResult<Void> updateLessonName(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改课程名称", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "课程id不能为空");
                AssertUtil.assertNotNull(request.getName(), ExceptionResultCode.ILLEGAL_PARAMETERS, "新课程名不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .lessonId(request.getLessonId())
                        .userId(request.getUserId())
                        .name(request.getName())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                lessonService.updateLessonName(lessonRequest);
                return RestUtil.buildSuccessResult(null, "课程名称修改成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/info")
    public ZCMUResult<Void> updateLessonInfo(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改课程介绍", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "课程id不能为空");
                AssertUtil.assertNotNull(request.getInfo(), ExceptionResultCode.ILLEGAL_PARAMETERS, "新课程介绍不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .lessonId(request.getLessonId())
                        .userId(request.getUserId())
                        .info(request.getInfo())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                lessonService.updateLessonInfo(lessonRequest);
                return RestUtil.buildSuccessResult(null, "课程介绍修改成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/pic")
    public ZCMUResult<Void> updateLessonPic(LessonRestRequest request, HttpServletRequest httpServletRequest
            , @PathParam("picFile") MultipartFile picFile) {
        return OperateTemplate.operate(log, "修改课程封面", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "课程id不能为空");
                AssertUtil.assertNotNull(picFile, ExceptionResultCode.ILLEGAL_PARAMETERS, "课程封面文件不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .lessonId(request.getLessonId())
                        .userId(request.getUserId())
                        .picFile(picFile)
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                lessonService.updateLessonPic(lessonRequest);
                return RestUtil.buildSuccessResult(null, "课程封面修改成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/passage/name")
    public ZCMUResult<Void> updatePassageName(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改课程章节名称", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getPassageId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "课程章节id不能为空");
                AssertUtil.assertNotNull(request.getName(), ExceptionResultCode.ILLEGAL_PARAMETERS, "新章节名不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .passageId(request.getPassageId())
                        .userId(request.getUserId())
                        .name(request.getName())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                lessonService.updatePassageLessonName(lessonRequest);
                return RestUtil.buildSuccessResult(null, "课程章节名称修改成功");
            }
        });
    }

    @CheckLogin
    @DeleteMapping
    public ZCMUResult<Void> deleteLesson(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "删除课程", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "删除的课程id不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                LessonRequest lessonRequest = LessonRequest.builder()
                        .lessonId(request.getLessonId())
                        .userId(request.getUserId())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                lessonService.deleteLesson(lessonRequest);
                return RestUtil.buildSuccessResult(null, "课程删除成功");
            }
        });
    }

    @CheckLogin
    @DeleteMapping("/passage")
    public ZCMUResult<Void> deletePassage(LessonRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "删除课程章节", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getPassageId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "要删除的章节id不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate() throws IOException {
                LessonRequest lessonRequest = LessonRequest.builder()
                        .passageId(request.getPassageId())
                        .userId(request.getUserId())
                        .build();
                lessonRequest.setVerifyId(request.getUserId());
                lessonService.deletePassage(lessonRequest);
                return RestUtil.buildSuccessResult(null, "课程章节删除成功");
            }
        });
    }
}
