package edu.hdu.hziee.betastudio.web.controller;

import edu.hdu.hziee.betastudio.business.lesson.model.HomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SubmitHomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.request.HomeworkRequest;
import edu.hdu.hziee.betastudio.business.lesson.service.HomeworkService;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateCallBack;
import edu.hdu.hziee.betastudio.util.resulttemplate.OperateTemplate;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.RestUtil;
import edu.hdu.hziee.betastudio.util.resulttemplate.restfulresult.ZCMUResult;
import edu.hdu.hziee.betastudio.web.aop.CheckLogin;
import edu.hdu.hziee.betastudio.web.request.HomeworkRestRequest;
import edu.hdu.hziee.betastudio.web.request.LessonRestRequest;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/homework")
public class HomeworkController {

    @Autowired
    HomeworkService homeworkService;

    @CheckLogin
    @PostMapping("/create")
    public ZCMUResult<HomeworkBO> createHomework(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "教师创建课程作业", request, httpServletRequest, new OperateCallBack<HomeworkBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "作业所属课程id不能为空");
                AssertUtil.assertNotNull(request.getName(), ExceptionResultCode.ILLEGAL_PARAMETERS, "作业名称不能为空");
                AssertUtil.assertNotNull(request.getInfo(), ExceptionResultCode.ILLEGAL_PARAMETERS, "作业内容不能为空");
                AssertUtil.assertNotNull(request.getStart(), ExceptionResultCode.ILLEGAL_PARAMETERS, "作业开始时间不能为空");
                AssertUtil.assertNotNull(request.getEnd(), ExceptionResultCode.ILLEGAL_PARAMETERS, "作业结束时间不能为空");
                Date start=new Date(request.getStart());
                Date end=new Date(request.getEnd());
                AssertUtil.assertTrue(end.after(start),ExceptionResultCode.ILLEGAL_PARAMETERS,"作业结束时间不能早于开始时间");
            }

            @Override
            public ZCMUResult<HomeworkBO> operate() throws IOException {
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .lessonId(request.getLessonId())
                        .userId(request.getUserId())
                        .name(request.getName())
                        .info(request.getInfo())
                        .start(new Date(request.getStart()))
                        .end(new Date(request.getEnd()))
                        .resourceList(request.getResourceList())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(homeworkService.createHomework(homeworkRequest), "课程作业创建成功");
            }
        });
    }

    /**
     *  该接口仅为备用接口，其功能已被囊括在下面的方法中，不推荐使用
     * {@link LessonController#getLesson(LessonRestRequest, HttpServletRequest)}
     */
    @CheckLogin
    @GetMapping("/lesson/homework")
    public ZCMUResult<List<HomeworkBO>> getLessonHomework(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "获取一门课程下的所有作业", request, httpServletRequest, new OperateCallBack<List<HomeworkBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getLessonId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "要查询的课程id不能为空");
            }

            @Override
            public ZCMUResult<List<HomeworkBO>> operate() throws IOException {
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .lessonId(request.getLessonId())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(homeworkService.getAllLessonHomework(homeworkRequest), "作业获取成功");
            }
        });
    }

    @CheckLogin
    @PostMapping("/submit")
    public ZCMUResult<SubmitHomeworkBO> createSubHomework(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "学生提交作业", request, httpServletRequest, new OperateCallBack<SubmitHomeworkBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "提交的作业id不能为空");
            }

            @Override
            public ZCMUResult<SubmitHomeworkBO> operate() throws IOException {
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .userId(request.getUserId())
                        .content(request.getContent())
                        .resourceList(request.getResourceList())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(homeworkService.createSubmitHomework(homeworkRequest), "作业提交成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/submit")
    public ZCMUResult<List<SubmitHomeworkBO>> getSubHomework(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "教师查看学生提交的作业", request, httpServletRequest, new OperateCallBack<List<SubmitHomeworkBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "查询的作业id不能为空");
            }

            @Override
            public ZCMUResult<List<SubmitHomeworkBO>> operate() throws IOException {
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(homeworkService.getAllSubHomework(homeworkRequest), "获取学生作业成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/me/subhomework")
    public ZCMUResult<SubmitHomeworkBO> getMySubHomework(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "学生查看该门作业下提交的作业", request, httpServletRequest, new OperateCallBack<SubmitHomeworkBO>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "查询的作业id不能为空");
            }

            @Override
            public ZCMUResult<SubmitHomeworkBO> operate() throws IOException {
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(homeworkService.getSelfSubmitHomework(homeworkRequest), "获取提交的作业成功");
            }
        });
    }

    @CheckLogin
    @GetMapping("/unsubmit")
    public ZCMUResult<List<AppUserInfoBO>> getUnSubHomeworkUser(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "教师查看未提交作业的学生信息", request, httpServletRequest, new OperateCallBack<List<AppUserInfoBO>>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "查询的作业id不能为空");
            }

            @Override
            public ZCMUResult<List<AppUserInfoBO>> operate() throws IOException {
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                return RestUtil.buildSuccessResult(homeworkService.getUnSubmitUserInfo(homeworkRequest), "获取未交作业学生信息成功");
            }
        });
    }

    @CheckLogin
    @PutMapping("/score")
    public ZCMUResult<Void> scoreHomework(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "教师为学生作业打分", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
                AssertUtil.assertNotNull(request.getSubmitHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "学生的作业id不能为空");
                AssertUtil.assertNotNull(request.getScore(), ExceptionResultCode.ILLEGAL_PARAMETERS, "所打分数不能为空");
            }

            @Override
            public ZCMUResult<Void> operate() throws IOException {
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .submitHomeworkId(request.getSubmitHomeworkId())
                        .score(request.getScore())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                homeworkService.scoreHomework(homeworkRequest);
                return RestUtil.buildSuccessResult(null, "打分成功！");
            }
        });
    }


}
