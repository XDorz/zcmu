package edu.hdu.hziee.betastudio.business.lesson.convert;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import edu.hdu.hziee.betastudio.business.lesson.model.HomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SubmitHomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.request.HomeworkRequest;
import edu.hdu.hziee.betastudio.business.lesson.service.HomeworkService;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.business.user.service.UserInfoService;
import edu.hdu.hziee.betastudio.dao.lesson.model.HomeworkDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.SubmitHomeworkDO;
import edu.hdu.hziee.betastudio.dao.lesson.repo.LessonDORepo;
import edu.hdu.hziee.betastudio.dao.user.model.UserInfoDO;
import edu.hdu.hziee.betastudio.dao.user.repo.UserInfoDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class HomeworkConvert {

    @Autowired
    UserInfoDORepo userInfoDORepo;

    @Autowired
    private LessonDORepo lessonDORepo;

    @Lazy
    @Autowired
    private UserInfoService userInfoService;

    @Autowired
    ResoursService resoursService;

    @Lazy
    @Autowired
    HomeworkService homeworkService;

    public HomeworkBO convert(HomeworkDO homeworkDO){
        AssertUtil.assertNotNull(homeworkDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(homeworkDO.getLessonId());
        UserInfoDO userInfoDO = userInfoDORepo.findAllByUserId(lessonDO.getUserId());
        return HomeworkBO.builder()
                .homeworkId(homeworkDO.getHomeworkId())
                .lessonId(homeworkDO.getLessonId())
                .lessonName(lessonDO.getLessonName())
                .creatorName(userInfoDO.getRealName())
                .info(homeworkDO.getInfo())
                .name(homeworkDO.getName())
                .start(homeworkDO.getStart())
                .end(homeworkDO.getEnd())
                .resoursBOList(resoursService.getListByBelongId(homeworkDO.getHomeworkId()))
                .ext(JSONObject.parseObject(lessonDO.getExt()).toJavaObject(new TypeReference<Map<String, String>>() {
                })).build();
    }

    public HomeworkDO convert(HomeworkBO homeworkBO){
        AssertUtil.assertNotNull(homeworkBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return HomeworkDO.builder()
                .homeworkId(homeworkBO.getHomeworkId()==null? IdUtil.getSnowflakeNextId(): homeworkBO.getHomeworkId())
                .info(homeworkBO.getInfo())
                .name(homeworkBO.getName())
                .start(homeworkBO.getStart())
                .end(homeworkBO.getEnd())
                .lessonId(homeworkBO.getLessonId())
                .deleted(false)
                .ext(JSONObject.toJSONString(homeworkBO.getExt()))
                .build();
    }

    public SubmitHomeworkBO convert(SubmitHomeworkDO submitHomeworkDO){
        AssertUtil.assertNotNull(submitHomeworkDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return SubmitHomeworkBO.builder()
                .homeworkId(submitHomeworkDO.getHomeworkId())
                .userId(submitHomeworkDO.getUserId())
                .user(userInfoService.getAppInfo(submitHomeworkDO.getUserId()))
                .content(submitHomeworkDO.getContent())
                .score(submitHomeworkDO.getScore())
                .submitId(submitHomeworkDO.getSubmitId())
                .resoursBOList(resoursService.getListByBelongId(submitHomeworkDO.getSubmitId()))
                .build();
    }

    public HomeworkBO convert(HomeworkDO homeworkDO,Long userId){
        HomeworkBO homeworkBO = convert(homeworkDO);
        //为其中的homework设置
        homeworkBO.setStatus(homeworkService.getHomeworkStatus(homeworkBO.getHomeworkId(),userId));
        HomeworkRequest homeworkRequest = HomeworkRequest.builder()
                .homeworkId(homeworkDO.getHomeworkId())
                .userId(userId)
                .build();
        homeworkRequest.setVerifyId(userId);
        homeworkBO.setSubHomework(homeworkService.getSelfSubmitHomework(homeworkRequest));
        return homeworkBO;
    }

    public SubmitHomeworkDO convert(SubmitHomeworkBO submitHomeworkBO){
        AssertUtil.assertNotNull(submitHomeworkBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return SubmitHomeworkDO.builder()
                .homeworkId(submitHomeworkBO.getHomeworkId())
                .submitId(submitHomeworkBO.getSubmitId()==null?IdUtil.getSnowflakeNextId(): submitHomeworkBO.getSubmitId())
                .content(submitHomeworkBO.getContent())
                .score(submitHomeworkBO.getScore())
                .userId(submitHomeworkBO.getUserId())
                .build();
    }
}
