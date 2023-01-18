package edu.hdu.hziee.betastudio.business.lesson.convert;

import cn.hutool.core.util.IdUtil;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import edu.hdu.hziee.betastudio.business.lesson.model.*;
import edu.hdu.hziee.betastudio.business.lesson.service.HomeworkService;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.business.user.service.UserInfoService;
import edu.hdu.hziee.betastudio.dao.lesson.model.*;
import edu.hdu.hziee.betastudio.dao.lesson.repo.LessonPassageDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class LessonConvert {

    @Autowired
    UserInfoService userInfoService;

    @Autowired
    ResoursService resoursService;

    @Autowired
    HomeworkService homeworkService;
    
    @Autowired
    LessonPassageDORepo lessonPassageDORepo;


    public LessonBO convert(LessonDO lessonDO){
        AssertUtil.assertNotNull(lessonDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        Long lessonId = lessonDO.getLessonId();
        //无法注入LessonService，会造成依赖循环
        List<LessonPassageBO> lessonPassageBOS = CollectionUtils.toStream(lessonPassageDORepo.findAllByLessonIdAndDeleted(lessonId,false))
                .filter(Objects::nonNull)
                .map(this::convert)
                .toList();
        return LessonBO.builder()
                .lessonId(lessonDO.getLessonId())
                .userId(lessonDO.getUserId())
                .picUrl(lessonDO.getPicUrl())
                .lessonName(lessonDO.getLessonName())
                .info(lessonDO.getInfo())
                .creater(userInfoService.getAppInfo(lessonDO.getUserId()))
                .homeworkBOList(homeworkService.getAllLessonHomework(lessonId))
                .lessonPassageBOList(lessonPassageBOS)
                .resoursBOList(resoursService.getListByBelongId(lessonId))
                .ext(JSONObject.parseObject(lessonDO.getExt()).toJavaObject(new TypeReference<Map<String, String>>() {
                })).build();
    }

    public LessonBO convert(LessonDO lessonDO,Long userId){
        LessonBO lessonBO = convert(lessonDO);
        //为其中的homework设置
        lessonBO.getHomeworkBOList().forEach(homeworkBO ->
                homeworkBO.setStatus(homeworkService.getHomeworkStatus(homeworkBO.getHomeworkId(),userId)));
        return lessonBO;
    }

    public LessonDO convert(LessonBO lessonBO){
        AssertUtil.assertNotNull(lessonBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return LessonDO.builder()
                .lessonId(lessonBO.getLessonId()==null? IdUtil.getSnowflakeNextId(): lessonBO.getLessonId())
                .lessonName(lessonBO.getLessonName())
                .userId(lessonBO.getUserId())
                .info(lessonBO.getInfo())
                .picUrl(lessonBO.getPicUrl())
                .deleted(false)
                .ext(JSONObject.toJSONString(lessonBO.getExt()))
                .build();
    }

    public LessonPassageBO convert(LessonPassageDO lessonPassageDO){
        AssertUtil.assertNotNull(lessonPassageDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return LessonPassageBO.builder()
                .lessonId(lessonPassageDO.getLessonId())
                .name(lessonPassageDO.getName())
                .passageId(lessonPassageDO.getPassageId())
                .resoursBOList(resoursService.getListByBelongId(lessonPassageDO.getPassageId()))
                .build();
    }
    
    public LessonPassageDO convert(LessonPassageBO lessonPassageBO){
        AssertUtil.assertNotNull(lessonPassageBO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return LessonPassageDO.builder()
                .passageId(lessonPassageBO.getPassageId()==null?IdUtil.getSnowflakeNextId(): lessonPassageBO.getPassageId())
                .lessonId(lessonPassageBO.getLessonId())
                .name(lessonPassageBO.getName())
                .deleted(false)
                .build();
    }

    public SimpleLessonBO convertSimple(LessonDO lessonDO){
        AssertUtil.assertNotNull(lessonDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"转换对象为空");
        return SimpleLessonBO.builder()
                .lessonId(lessonDO.getLessonId())
                .lessonName(lessonDO.getLessonName())
                .userId(lessonDO.getUserId())
                .info(lessonDO.getInfo())
                .creater(userInfoService.getAppInfo(lessonDO.getUserId()))
                .picUrl(lessonDO.getPicUrl())
                .build();
    }
}
