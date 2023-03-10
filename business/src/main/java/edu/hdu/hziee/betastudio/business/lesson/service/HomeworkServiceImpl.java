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
        //todo ?????????????????????APPUserInfo????????? ??????????????????subId???????????????????????????
        //??????????????????????????????/?????????
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(), homeworkDO.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"???????????????????????????????????????");
        return CollectionUtils.toStream(submitHomeworkDORepo.findAllByHomeworkId(request.getHomeworkId()))
                .filter(Objects::nonNull)
                .map(homeworkConvert::convert)
                .toList();
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public HomeworkBO createHomework(HomeworkRequest request) {
        AssertUtil.assertTrue(lessonVerifyOperate.verifyLevel(request.getVerifyId(),request.getLessonId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"???????????????????????????????????????");

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

        //????????????
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
        //????????????????????????????????????
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        Date date=new Date();
        AssertUtil.assertTrue(date.after(homeworkDO.getStart()),ExceptionResultCode.FORBIDDEN,"????????????????????????");
        AssertUtil.assertTrue(date.before(homeworkDO.getEnd()),ExceptionResultCode.FORBIDDEN,"????????????????????????");

        //?????????????????????????????????
        boolean b = lessonUserRelationDORepo.existsByLessonIdAndUserId(homeworkDO.getLessonId(), request.getUserId());
        AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"??????????????????????????????");

        //????????????????????????
        SubmitHomeworkDO beforeSub = submitHomeworkDORepo.findAllByHomeworkIdAndUserId(
                request.getHomeworkId(), request.getVerifyId());
        AssertUtil.assertNull(beforeSub,ExceptionResultCode.ILLEGAL_PARAMETERS,"???????????????????????????????????????????????????");

        //????????????????????????
        long submitHomeworkId = IdUtil.getSnowflakeNextId();
        SubmitHomeworkDO submitHomeworkDO = SubmitHomeworkDO.builder()
                .submitId(submitHomeworkId)
                .content(request.getInfo())
                .homeworkId(request.getHomeworkId())
                .userId(request.getUserId())
                .score(null)
                .build();
        submitHomeworkDORepo.save(submitHomeworkDO);

        //????????????
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
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"????????????????????????????????????????????????");

        //?????????????????????????????????????????????
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        List<LessonUserRelationDO> lessonUsers = lessonUserRelationDORepo.findAllByLessonId(homeworkDO.getLessonId());
        List<SubmitHomeworkDO> submittedUsers = submitHomeworkDORepo.findAllByHomeworkId(request.getHomeworkId());
        //???????????????????????????????????????
        Set<Long> set=new HashSet<Long>();
        submittedUsers.forEach(submitHomeworkDO -> set.add(submitHomeworkDO.getUserId()));
        Iterator<LessonUserRelationDO> ite = lessonUsers.iterator();
        //??????????????????????????????????????????????????????
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
        AssertUtil.assertNotNull(subHomeworkDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"???????????????id????????????????????????");
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),subHomeworkDO.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"??????????????????????????????");

        AssertUtil.assertNotNull(request.getScore(),ExceptionResultCode.ILLEGAL_PARAMETERS,"?????????????????????NULL??????");
        submitHomeworkDORepo.scoreHomework(request.getSubmitHomeworkId(),request.getScore());
    }

    @Override
    public HomeworkStatusEnum getHomeworkStatus(Long homeworkId, Long userId) {
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(homeworkId);
        AssertUtil.assertNotNull(homeworkDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"??????????????????");
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
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"????????????????????????????????????");

        return homeworkDORepo.updateHomeworkName(request.getHomeworkId(),request.getName());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)

    public Integer updateHomeworkInfo(HomeworkRequest request) {
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"????????????????????????????????????");

        return homeworkDORepo.updateHomeworkInfo(request.getHomeworkId(),request.getInfo());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateHomeworkTime(HomeworkRequest request) {
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"??????????????????????????????????????????");

        return homeworkDORepo.updateHomeworkTime(request.getHomeworkId(), request.getStart(),request.getEnd());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteHomework(HomeworkRequest request) {
        AssertUtil.assertTrue(homeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getHomeworkId())
                .hasPerm(OperateLevelEnum.MEDIUM_OPERATE),ExceptionResultCode.FORBIDDEN,"???????????????????????????");

        return homeworkDORepo.deleteHomework(request.getHomeworkId(),true);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateSubHomeworkContent(HomeworkRequest request) {
        AssertUtil.assertTrue(subHomeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getSubmitHomeworkId())
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"?????????????????????????????????????????????");

        Long submitHomeworkId = request.getSubmitHomeworkId();
        //????????????????????????????????????DO????????????????????????????????????????????????
        SubmitHomeworkDO submitHomeworkDO = submitHomeworkDORepo.findAllBySubmitId(submitHomeworkId);
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(submitHomeworkDO.getHomeworkId());
        AssertUtil.assertTrue(homeworkDO.getStart().before(new Date()),ExceptionResultCode.FORBIDDEN,"???????????????????????????");
        AssertUtil.assertTrue(homeworkDO.getEnd().after(new Date()),ExceptionResultCode.FORBIDDEN,"???????????????????????????");
        Long lessonId = homeworkDO.getLessonId();
        boolean b = lessonUserRelationDORepo.existsByLessonIdAndUserId(lessonId, request.getVerifyId());
        AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"??????????????????????????????????????????");

        return submitHomeworkDORepo.updateContent(request.getSubmitHomeworkId(),request.getContent());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateSubHomeworkResource(HomeworkRequest request) {
        AssertUtil.assertTrue(subHomeworkVerifyOperate.verifyLevel(request.getVerifyId(),request.getSubmitHomeworkId())
                .hasPerm(OperateLevelEnum.TOTAL_OPERATE),ExceptionResultCode.FORBIDDEN,"?????????????????????????????????????????????");

        Long submitHomeworkId = request.getSubmitHomeworkId();
        //????????????????????????????????????DO????????????????????????????????????????????????
        SubmitHomeworkDO submitHomeworkDO = submitHomeworkDORepo.findAllBySubmitId(submitHomeworkId);
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(submitHomeworkDO.getHomeworkId());
        AssertUtil.assertTrue(homeworkDO.getStart().before(new Date()),ExceptionResultCode.FORBIDDEN,"???????????????????????????");
        AssertUtil.assertTrue(homeworkDO.getEnd().after(new Date()),ExceptionResultCode.FORBIDDEN,"???????????????????????????");
        Long lessonId = homeworkDO.getLessonId();
        boolean b = lessonUserRelationDORepo.existsByLessonIdAndUserId(lessonId, request.getVerifyId());
        AssertUtil.assertTrue(b,ExceptionResultCode.FORBIDDEN,"??????????????????????????????????????????");

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


    //==================================????????????????????????????????????======================================================
    private OperateLevelEnum customHomeworkVerify(Long userId,Long homeworkId){
        HomeworkDO homeworkDO = homeworkDORepo.findAllByHomeworkId(homeworkId);
        if (homeworkDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        LessonDO lessonDO = lessonDORepo.findAllByLessonId(homeworkDO.getLessonId());
        if (lessonDO == null) {
            return OperateLevelEnum.FORBIDDEN;
        }

        //????????????????????????????????????????????????
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

        //??????????????????????????????????????????
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
