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
            public ZCMUResult<HomeworkBO> operate(){
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
            public ZCMUResult<List<HomeworkBO>> operate(){
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
            public ZCMUResult<SubmitHomeworkBO> operate(){
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
            public ZCMUResult<List<SubmitHomeworkBO>> operate(){
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
            public ZCMUResult<SubmitHomeworkBO> operate(){
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
            public ZCMUResult<List<AppUserInfoBO>> operate(){
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
            public ZCMUResult<Void> operate(){
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

    @CheckLogin
    @PutMapping("/name")
    public ZCMUResult<Void> updateHomeworkName(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改作业名称", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "修改的作业id不能为空");
                AssertUtil.assertNotNull(request.getName(), ExceptionResultCode.ILLEGAL_PARAMETERS, "修改的作业名称不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .name(request.getName())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                homeworkService.updateHomeworkName(homeworkRequest);
                return RestUtil.buildSuccessResult(null, "作业名称修改成功！");
            }
        });
    }

    @CheckLogin
    @PutMapping("/info")
    public ZCMUResult<Void> updateHomeworkInfo(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改作业要求", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "修改的作业id不能为空");
                AssertUtil.assertNotNull(request.getInfo(), ExceptionResultCode.ILLEGAL_PARAMETERS, "修改的作业要求能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .info(request.getInfo())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                homeworkService.updateHomeworkInfo(homeworkRequest);
                return RestUtil.buildSuccessResult(null, "作业要求修改成功！");
            }
        });
    }

    @CheckLogin
    @PutMapping("/time")
    public ZCMUResult<Void> updateHomeworkTime(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改作业起止时间", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "修改的作业id不能为空");
                AssertUtil.assertNotNull(request.getStart(), ExceptionResultCode.ILLEGAL_PARAMETERS, "修改的作业开始时间不能为空");
                AssertUtil.assertNotNull(request.getEnd(), ExceptionResultCode.ILLEGAL_PARAMETERS, "修改的作业结束时间不能为空");
                Date start=new Date(request.getStart());
                Date end=new Date(request.getEnd());
                AssertUtil.assertTrue(start.before(end),ExceptionResultCode.ILLEGAL_PARAMETERS,"修改的作业开始时间不能比结束时间晚");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .start(new Date(request.getStart()))
                        .end(new Date(request.getEnd()))
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                homeworkService.updateHomeworkTime(homeworkRequest);
                return RestUtil.buildSuccessResult(null, "作业起止时间修改成功！");
            }
        });
    }

    @CheckLogin
    @DeleteMapping
    public ZCMUResult<Void> deleteHomework(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "撤销作业", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "撤销的作业id不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .homeworkId(request.getHomeworkId())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                homeworkService.deleteHomework(homeworkRequest);
                return RestUtil.buildSuccessResult(null, "作业撤销成功！");
            }
        });
    }

    @CheckLogin
    @PutMapping("/sub/content")
    public ZCMUResult<Void> updateSubHomeworkContent(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改提交的作业内容", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getSubmitHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "提交作业id不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .submitHomeworkId(request.getSubmitHomeworkId())
                        .content(request.getContent())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                homeworkService.updateSubHomeworkContent(homeworkRequest);
                return RestUtil.buildSuccessResult(null, "回答修改成功！");
            }
        });
    }

    @CheckLogin
    @PutMapping("/sub/resource")
    public ZCMUResult<Void> updateSubHomeworkResource(HomeworkRestRequest request, HttpServletRequest httpServletRequest) {
        return OperateTemplate.operate(log, "修改提交的作业的附件", request, httpServletRequest, new OperateCallBack<Void>() {
            @Override
            public void before() {
                AssertUtil.assertNotNull(request, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(httpServletRequest, ExceptionResultCode.ILLEGAL_PARAMETERS, "请求不能为空");
                AssertUtil.assertNotNull(request.getSubmitHomeworkId(), ExceptionResultCode.ILLEGAL_PARAMETERS, "提交作业id不能为空");
                AssertUtil.assertNotNull(request.getUserId(), ExceptionResultCode.UNAUTHORIZED, "用户未登录");
            }

            @Override
            public ZCMUResult<Void> operate(){
                HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                        .submitHomeworkId(request.getSubmitHomeworkId())
                        .deletedResourceList(request.getDeletedResourceList())
                        .resourceList(request.getResourceList())
                        .userId(request.getUserId())
                        .build();
                homeworkRequest.setVerifyId(request.getUserId());
                homeworkService.updateSubHomeworkResource(homeworkRequest);
                return RestUtil.buildSuccessResult(null, "附件修改成功");
            }
        });
    }
}
