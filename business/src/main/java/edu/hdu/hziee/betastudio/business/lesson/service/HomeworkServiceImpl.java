package edu.hdu.hziee.betastudio.business.lesson.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.lesson.convert.HomeworkConvert;
import edu.hdu.hziee.betastudio.business.lesson.model.HomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SubmitHomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.request.HomeworkRequest;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.business.user.service.UserInfoService;
import edu.hdu.hziee.betastudio.dao.lesson.model.HomeworkDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonUserRelationDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.SubmitHomeworkDO;
import edu.hdu.hziee.betastudio.dao.lesson.repo.HomeworkDORepo;
import edu.hdu.hziee.betastudio.dao.lesson.repo.LessonDORepo;
import edu.hdu.hziee.betastudio.dao.lesson.repo.LessonUserRelationDORepo;
import edu.hdu.hziee.betastudio.dao.lesson.repo.SubmitHomeworkDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.HomeworkStatusEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@Service
public class HomeworkServiceImpl implements HomeworkService{
    @Autowired
    private LessonDORepo lessonDORepo;

    @Autowired
    HomeworkDORepo homeworkDORepo;

    @Autowired
    HomeworkConvert homeworkConvert;

    @Autowired
    SubmitHomeworkDORepo submitHomeworkDORepo;

    @Autowired
    LessonUserRelationDORepo lessonUserRelationDORepo;

    @Autowired
    ResoursService resoursService;

    @Lazy
    @Autowired
    UserInfoService userinfoService;

    @Override
    public List<HomeworkBO> getAllLessonHomework(Long lessonId) {
        return CollectionUtils.toStream(homeworkDORepo.findAllByLessonIdAndDeletedOrderByEndDesc(lessonId,false))
                .filter(Objects::nonNull)
                .map(homeworkConvert::convert)
                .toList();
    }

    @Override
    public List<HomeworkBO> getAllLessonHomework(HomeworkRequest request) {
        List<HomeworkBO> homeworkBOList = getAllLessonHomework(request.getLessonId());
        HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                .userId(request.getUserId())
                .build();
        homeworkBOList.forEach(
                homeworkBO -> {
                    homeworkBO.setStatus(getHomeworkStatus(homeworkBO.getHomeworkId(),request.getUserId()));
                    homeworkRequest.setHomeworkId(homeworkBO.getHomeworkId());
                    homeworkBO.setSubHomework(getSelfSubmitHomework(homeworkRequest));
                }
        );
        return homeworkBOList;
    }

    @Override
    public List<SubmitHomeworkBO> getAllSubHomework(HomeworkRequest request) {
        //验证是否是课程管理员/总管理
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(homeworkDO.getLessonId());
        //todo 为总管理开绿灯
        AssertUtil.assertEquals(lessonDO.getUserId(),request.getUserId(),ExceptionResultCode.FORBIDDEN,"您无权查看提交的作业(ERROR:您不是该课程的主管)");
        return CollectionUtils.toStream(submitHomeworkDORepo.findAllByHomeworkId(request.getHomeworkId()))
                .filter(Objects::nonNull)
                .map(homeworkConvert::convert)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkBO createHomework(HomeworkRequest request) {
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(request.getLessonId());
        //todo 为总管理开绿灯
        AssertUtil.assertEquals(lessonDO.getUserId(),request.getUserId()
                ,ExceptionResultCode.FORBIDDEN,"您无权发布作业(ERROR:您不是该课程的主管)");

        Long homeworkId= IdUtil.getSnowflakeNextId();
        HomeworkDO homeworkDO = HomeworkDO.builder()
                .homeworkId(homeworkId)
                .start(request.getStart())
                .end(request.getEnd())
                //todo 检测XSS攻击
                .info(request.getInfo())
                //todo 检测XSS攻击
                .name(request.getName())
                .lessonId(request.getLessonId())
                .deleted(false)
                .ext("{}")
                .build();
        homeworkDORepo.save(homeworkDO);

        //关联资源
        ResoursRequest resoursRequest = ResoursRequest.builder()
                .resourceList(request.getResourceList())
                .belongId(homeworkId)
                .build();
        resoursService.connectResource(resoursRequest);
        return homeworkConvert.convert(homeworkDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public SubmitHomeworkBO createSubmitHomework(HomeworkRequest request) {
        //检查是否在作业提交时间内
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        Date date=new Date();
        AssertUtil.assertTrue(date.after(homeworkDO.getStart()),ExceptionResultCode.FORBIDDEN,"该作业还未开始！");
        AssertUtil.assertTrue(date.before(homeworkDO.getEnd()),ExceptionResultCode.FORBIDDEN,"该作业已经截止！");

        //验证是否重复提交
        SubmitHomeworkDO beforeSub = submitHomeworkDORepo.findAllByHomeworkIdAndUserId(
                request.getHomeworkId(), request.getVerifyId());
        AssertUtil.assertNull(beforeSub,ExceptionResultCode.ILLEGAL_PARAMETERS,"您已提交过一次作业，无法重复提交！");

        long submitHomeworkId = IdUtil.getSnowflakeNextId();
        SubmitHomeworkDO submitHomeworkDO = SubmitHomeworkDO.builder()
                .submitId(submitHomeworkId)
                //todo 检测XSS攻击
                .content(request.getInfo())
                .homeworkId(request.getHomeworkId())
                .userId(request.getUserId())
                .score(null)
                .build();
        submitHomeworkDORepo.save(submitHomeworkDO);

        //关联资源
        ResoursRequest resoursRequest = ResoursRequest.builder()
                .resourceList(request.getResourceList())
                .belongId(submitHomeworkId)
                .build();
        resoursService.connectResource(resoursRequest);
        return homeworkConvert.convert(submitHomeworkDO);
    }

    @Override
    public SubmitHomeworkBO getSelfSubmitHomework(HomeworkRequest request) {
        SubmitHomeworkDO submitHomeworkDO = submitHomeworkDORepo.findAllByHomeworkIdAndUserId(
                request.getHomeworkId(), request.getUserId());
        return homeworkConvert.convert(submitHomeworkDO);
    }

    @Override
    public List<AppUserInfoBO> getUnSubmitUserInfo(HomeworkRequest request) {
        Long homeworkId = request.getHomeworkId();
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(homeworkDO.getLessonId());
        AssertUtil.assertNotNull(lessonDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"查无该作业的所属课程");
        //todo 对总管理开绿灯
        AssertUtil.assertEquals(lessonDO.getUserId(),request.getVerifyId(),ExceptionResultCode.FORBIDDEN,"您不是该课程的创建者,无权查看！");

        //获取选课学生和提交了作业的学生
        List<LessonUserRelationDO> lessonUsers = lessonUserRelationDORepo.findAllByLessonId(homeworkDO.getLessonId());
        List<SubmitHomeworkDO> submittedUsers = submitHomeworkDORepo.findAllByHomeworkId(homeworkId);
        //提取出提交了作业的学生学号
        Set<Long> set=new HashSet<Long>();
        submittedUsers.forEach(submitHomeworkDO -> set.add(submitHomeworkDO.getUserId()));
        Iterator<LessonUserRelationDO> ite = lessonUsers.iterator();
        //去重，选课学生列表去掉提交了作业的人
        while (ite.hasNext()){
            LessonUserRelationDO relationDO = ite.next();
            Long userId = relationDO.getUserId();
            if(set.contains(userId)){
                ite.remove();
            }
        }

        List<AppUserInfoBO> result=new ArrayList<>();
        for (LessonUserRelationDO lessonUser : lessonUsers) {
            result.add(userinfoService.getAppInfo(lessonUser.getUserId()));
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void scoreHomework(HomeworkRequest request) {
        SubmitHomeworkDO subHomeworkDO = submitHomeworkDORepo.findAllBySubmitId(request.getSubmitHomeworkId());
        AssertUtil.assertNotNull(subHomeworkDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"未查询到该id对应的学生作业！");

        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(subHomeworkDO.getHomeworkId());
        AssertUtil.assertNotNull(subHomeworkDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"未查询到对应的作业！");

        LessonDO lessonDO = lessonDORepo.findAllByLessonId(homeworkDO.getLessonId());
        AssertUtil.assertNotNull(subHomeworkDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"未查询到发布该作业的课程！");

        //todo 为管理员开绿灯
        AssertUtil.assertEquals(lessonDO.getUserId(),request.getVerifyId()
                ,ExceptionResultCode.FORBIDDEN,"您不是该作业的发布者，无权为其打分！");

        AssertUtil.assertNotNull(request.getScore(),ExceptionResultCode.ILLEGAL_PARAMETERS,"无法给作业打【NULL】分");
        submitHomeworkDORepo.scoreHomework(request.getSubmitHomeworkId(),request.getScore());
    }

    @Override
    public HomeworkStatusEnum getHomeworkStatus(Long homeworkId, Long userId) {
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(homeworkId);
        AssertUtil.assertNotNull(homeworkDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"查无此作业！");
        boolean hasRelation = lessonUserRelationDORepo.existsByLessonIdAndUserId(homeworkDO.getLessonId(), userId);
        if(!hasRelation){
            return HomeworkStatusEnum.CUSTOMER;
        }
        SubmitHomeworkDO submitHomeworkDO = submitHomeworkDORepo.findAllByHomeworkIdAndUserId(homeworkId, userId);
        if(submitHomeworkDO==null){
            return HomeworkStatusEnum.UN_SUBMIT;
        }
        Integer score = submitHomeworkDO.getScore();
        if(score==null){
            return HomeworkStatusEnum.SUBMIT_NOT_SCORE;
        }
        if(score<0){
            return HomeworkStatusEnum.KICK_BACK;
        }
        return HomeworkStatusEnum.SUBMIT_SCORED;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //todo 检测XSS攻击
    public Integer updateHomeworkName(HomeworkRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //todo 检测XSS攻击
    public Integer updateHomeworkInfo(HomeworkRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateHomeworkTime(HomeworkRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteHomework(HomeworkRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //todo 检测XSS攻击
    public Integer updateSubHomeworkContent(HomeworkRequest request) {
        return null;
    }


}
