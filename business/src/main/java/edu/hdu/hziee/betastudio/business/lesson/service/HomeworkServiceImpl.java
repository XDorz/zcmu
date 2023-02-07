package edu.hdu.hziee.betastudio.business.lesson.service;

import cn.hutool.core.util.IdUtil;
import edu.hdu.hziee.betastudio.business.lesson.convert.HomeworkConvert;
import edu.hdu.hziee.betastudio.business.lesson.model.HomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SubmitHomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.request.HomeworkRequest;
import edu.hdu.hziee.betastudio.business.perm.service.PermService;
import edu.hdu.hziee.betastudio.business.perm.verify.VerifyOperate;
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
import edu.hdu.hziee.betastudio.util.customenum.OperateLevelEnum;
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

    @Autowired
    PermService permService;

    @Lazy
    @Autowired
    UserInfoService userinfoService;

    @Autowired
    private void setVerify(VerifyOperate verifyOperate){
        this.homeworkVerifyOperate=verifyOperate.getInstance(this::customHomeworkVerify);
        this.subHomeworkVerifyOperate=verifyOperate.getInstance(this::customSubHomeworkVerify);
        this.lessonVerifyOperate=VerifyOperate.getInstance(this::customLessonVerify);
    }
    private VerifyOperate homeworkVerifyOperate;
    private VerifyOperate subHomeworkVerifyOperate;
    private VerifyOperate lessonVerifyOperate;

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
        //todo 将其返回值改为APPUserInfo？？？ 增加一个通过subId获取作业详情的方法
        //验证是否是课程管理员/总管理
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(), homeworkDO.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权查看该作业的提交信息");
        return CollectionUtils.toStream(submitHomeworkDORepo.findAllByHomeworkId(request.getHomeworkId()))
                .filter(Objects::nonNull)
                .map(homeworkConvert::convert)
                .toList();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkBO createHomework(HomeworkRequest request) {
        AssertUtil.assertTrue(lessonVerifyOperate.verifyLevel(request.getVerifyId(),request.getLessonId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权在该课程下发布作业！");

        Long homeworkId= IdUtil.getSnowflakeNextId();
        HomeworkDO homeworkDO = HomeworkDO.builder()
                .homeworkId(homeworkId)
                .start(request.getStart())
                .end(request.getEnd())
                .info(request.getInfo())
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

        //验证该学生是此课程学生
        boolean b = lessonUserRelationDORepo.existsByLessonIdAndUserId(homeworkDO.getLessonId(), request.getUserId());
        AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"您不是此课程的学生！");

        //验证是否重复提交
        SubmitHomeworkDO beforeSub = submitHomeworkDORepo.findAllByHomeworkIdAndUserId(
                request.getHomeworkId(), request.getVerifyId());
        AssertUtil.assertNull(beforeSub,ExceptionResultCode.ILLEGAL_PARAMETERS,"您已提交过一次作业，无法重复提交！");

        //创建作业提交信息
        long submitHomeworkId = IdUtil.getSnowflakeNextId();
        SubmitHomeworkDO submitHomeworkDO = SubmitHomeworkDO.builder()
                .submitId(submitHomeworkId)
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
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权查看本作业未提交学生的名单");

        //获取选课学生和提交了作业的学生
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        List<LessonUserRelationDO> lessonUsers = lessonUserRelationDORepo.findAllByLessonId(homeworkDO.getLessonId());
        List<SubmitHomeworkDO> submittedUsers = submitHomeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
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
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),subHomeworkDO.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权为该作业打分！");

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
    public Integer updateHomeworkName(HomeworkRequest request) {
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权修改该作业的名称！");

        return homeworkDORepo.updateHomeworkName(request.getHomeworkId(),request.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)

    public Integer updateHomeworkInfo(HomeworkRequest request) {
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权修改该作业的内容！");

        return homeworkDORepo.updateHomeworkInfo(request.getHomeworkId(),request.getInfo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateHomeworkTime(HomeworkRequest request) {
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权修改该作业的起止时间！");

        return homeworkDORepo.updateHomeworkTime(request.getHomeworkId(), request.getStart(),request.getEnd());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteHomework(HomeworkRequest request) {
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权撤销该作业！");

        return homeworkDORepo.deleteHomework(request.getHomeworkId(),true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateSubHomeworkContent(HomeworkRequest request) {
        AssertUtil.assertTrue(subHomeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getSubmitHomeworkId())
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权修改该提交的作业的内容！");

        Long submitHomeworkId = request.getSubmitHomeworkId();
        //上面鉴权方法中以验证过该DO的存在性，故不用再次验证其存在性
        SubmitHomeworkDO submitHomeworkDO = submitHomeworkDORepo.findAllBySubmitId(submitHomeworkId);
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(submitHomeworkDO.getHomeworkId());
        AssertUtil.assertTrue(homeworkDO.getStart().before(new Date()),ExceptionResultCode.FORBIDDEN,"还未到作业提交时间");
        AssertUtil.assertTrue(homeworkDO.getEnd().after(new Date()),ExceptionResultCode.FORBIDDEN,"该作业已经结束提交");
        Long lessonId = homeworkDO.getLessonId();
        boolean b = lessonUserRelationDORepo.existsByLessonIdAndUserId(lessonId, request.getVerifyId());
        AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"您不是本项作业所属课程的学生");

        return submitHomeworkDORepo.updateContent(request.getSubmitHomeworkId(),request.getContent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSubHomeworkResource(HomeworkRequest request) {
        AssertUtil.assertTrue(subHomeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getSubmitHomeworkId())
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"您无权修改该提交的作业的内容！");

        Long submitHomeworkId = request.getSubmitHomeworkId();
        //上面鉴权方法中以验证过该DO的存在性，故不用再次验证其存在性
        SubmitHomeworkDO submitHomeworkDO = submitHomeworkDORepo.findAllBySubmitId(submitHomeworkId);
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(submitHomeworkDO.getHomeworkId());
        AssertUtil.assertTrue(homeworkDO.getStart().before(new Date()),ExceptionResultCode.FORBIDDEN,"还未到作业提交时间");
        AssertUtil.assertTrue(homeworkDO.getEnd().after(new Date()),ExceptionResultCode.FORBIDDEN,"该作业已经结束提交");
        Long lessonId = homeworkDO.getLessonId();
        boolean b = lessonUserRelationDORepo.existsByLessonIdAndUserId(lessonId, request.getVerifyId());
        AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"您不是本项作业所属课程的学生");

        ResoursRequest resoursRequest = ResoursRequest.builder()
                .userId(request.getVerifyId())
                .belongId(submitHomeworkId)
                .build();
        resoursRequest.setVerifyId(request.getVerifyId());
        resoursRequest.setSkipVerify(true);

        for (Long deleteId : request.getDeletedResourceList()) {
            resoursRequest.setResourceId(deleteId);
            resoursService.deleteResource(resoursRequest);
        }
        resoursRequest.setResourceList(request.getResourceList());
        resoursRequest.setResourceId(null);
        resoursService.connectResource(resoursRequest);
    }


    //==================================以下为归属于权限验证代码======================================================
    private OperateLevelEnum customHomeworkVerify(Long userId,Long homeworkId){
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(homeworkId);
        if (homeworkDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        LessonDO lessonDO = lessonDORepo.findAllByLessonId(homeworkDO.getLessonId());
        if (lessonDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        //课程创建者可以完全操作发布的作业
        if (userId.equals(lessonDO.getUserId())) {
            return OperateLevelEnum.TOTAL_OPERATE;
        }
        return null;
    }

    private OperateLevelEnum customSubHomeworkVerify(Long userId,Long subHomeworkId){
        SubmitHomeworkDO submitHomeworkDO = submitHomeworkDORepo.findAllBySubmitId(subHomeworkId);
        if (submitHomeworkDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }
        if(userId.equals(submitHomeworkDO.getUserId())){
            return OperateLevelEnum.TOTAL_OPERATE;
        }

        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(submitHomeworkDO.getHomeworkId());
        if (homeworkDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        LessonDO lessonDO = lessonDORepo.findAllByLessonId(homeworkDO.getLessonId());
        if (lessonDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        //课程创建者部分操作提交的作业
        if (userId.equals(lessonDO.getUserId())) {
            return OperateLevelEnum.MEDIUM_OPERATE;
        }
        return null;
    }

    private OperateLevelEnum customLessonVerify(Long userId,Long lessonId){
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(lessonId);
        if (lessonDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        if (userId.equals(lessonDO.getUserId())) {
            return OperateLevelEnum.TOTAL_OPERATE;
        }
        return null;
    }
}
