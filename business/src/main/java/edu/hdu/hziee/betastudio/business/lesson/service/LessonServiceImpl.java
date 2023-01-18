package edu.hdu.hziee.betastudio.business.lesson.service;

import cn.hutool.core.util.IdUtil;
import com.alibaba.excel.EasyExcel;
import edu.hdu.hziee.betastudio.business.lesson.convert.LessonConvert;
import edu.hdu.hziee.betastudio.business.lesson.model.LessonBO;
import edu.hdu.hziee.betastudio.business.lesson.model.LessonPassageBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SimpleLessonBO;
import edu.hdu.hziee.betastudio.business.lesson.model.UserLessonExcelBO;
import edu.hdu.hziee.betastudio.business.lesson.request.LessonRequest;
import edu.hdu.hziee.betastudio.business.resours.request.ResoursRequest;
import edu.hdu.hziee.betastudio.business.resours.service.ResoursService;
import edu.hdu.hziee.betastudio.business.user.model.UserInfoBO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonPassageDO;
import edu.hdu.hziee.betastudio.dao.lesson.model.LessonUserRelationDO;
import edu.hdu.hziee.betastudio.dao.lesson.repo.LessonDORepo;
import edu.hdu.hziee.betastudio.dao.lesson.repo.LessonPassageDORepo;
import edu.hdu.hziee.betastudio.dao.lesson.repo.LessonUserRelationDORepo;
import edu.hdu.hziee.betastudio.dao.user.model.UserInfoDO;
import edu.hdu.hziee.betastudio.dao.user.repo.UserInfoDORepo;
import edu.hdu.hziee.betastudio.util.common.AssertUtil;
import edu.hdu.hziee.betastudio.util.common.CollectionUtils;
import edu.hdu.hziee.betastudio.util.common.EasyExcelListener;
import edu.hdu.hziee.betastudio.util.common.ZCMUException;
import edu.hdu.hziee.betastudio.util.customenum.ExceptionResultCode;
import edu.hdu.hziee.betastudio.util.customenum.basic.ZCMUConstant;
import edu.hdu.hziee.betastudio.util.tecentcos.CosUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@Slf4j
@Service
public class LessonServiceImpl implements LessonService{
    @Autowired
    private UserInfoDORepo userInfoDORepo;
    @Autowired
    private LessonPassageDORepo lessonPassageDORepo;
    @Autowired
    private LessonDORepo lessonDORepo;

    @Autowired
    CosUtil cosUtil;

    @Autowired
    LessonUserRelationDORepo lessonUserRelationDORepo;

    @Autowired
    LessonConvert convert;

    @Autowired
    HomeworkService homeworkService;

    @Autowired
    ResoursService resoursService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LessonBO createLesson(LessonRequest request) {
        String picUrl= ZCMUConstant.LESSON_PIC_URL;
        if(request.getPicFile()!=null){
            picUrl=cosUtil.uploadFile(request.getPicFile());
        }
        long lessonId = IdUtil.getSnowflakeNextId();
        LessonDO lessonDO = LessonDO.builder()
                .lessonId(lessonId)
                //todo 检测XSS攻击
                .lessonName(request.getName())
                .picUrl(picUrl)
                //todo 检测XSS攻击
                .info(request.getInfo())
                .userId(request.getUserId())
                .deleted(false)
                .ext("{}")
                .build();
        lessonDORepo.save(lessonDO);

        //关联资源
        ResoursRequest resoursRequest = ResoursRequest.builder()
                .resourceList(request.getResourceList())
                .belongId(lessonId)
                .build();
        resoursService.connectResource(resoursRequest);

        return convert.convert(lessonDO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public LessonPassageBO createPassageLesson(LessonRequest request) {
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(request.getLessonId());
        //todo 为总管理开绿灯
        AssertUtil.assertEquals(lessonDO.getUserId(),request.getVerifyId()
                ,ExceptionResultCode.FORBIDDEN,"您不是该课程的创建者，无法为其添加章节");

        long passageId = IdUtil.getSnowflakeNextId();
        LessonPassageDO lessonPassageDO = LessonPassageDO.builder()
                .passageId(passageId)
                .lessonId(request.getLessonId())
                //todo 检测XSS攻击
                .name(request.getName())
                .deleted(false)
                .build();
        lessonPassageDORepo.save(lessonPassageDO);

        //关联资源
        ResoursRequest resoursRequest = ResoursRequest.builder()
                .resourceList(request.getResourceList())
                .belongId(passageId)
                .build();
        resoursService.connectResource(resoursRequest);
        return convert.convert(lessonPassageDO);
    }

    @Override
    public List<SimpleLessonBO> getAllChooseLesson(LessonRequest request) {
        List<LessonUserRelationDO> relationDOList = lessonUserRelationDORepo.findAllByUserId(request.getUserId());
        List<SimpleLessonBO> lessonBOList=new ArrayList<>();
        for (LessonUserRelationDO relationDO : relationDOList) {
            LessonDO lessonDO = lessonDORepo.findAllByLessonId(relationDO.getLessonId());
            if(!lessonDO.isDeleted()){
                lessonBOList.add(convert.convertSimple(lessonDO));
            }
        }
        return lessonBOList;
    }

    @Override
    public List<SimpleLessonBO> getAllCreateLesson(LessonRequest request) {
        List<LessonDO> lessonDOS = lessonDORepo.findAllByUserId(request.getUserId());
        List<SimpleLessonBO> lessonBOList=new ArrayList<>();
        for (LessonDO lessonDO : lessonDOS) {
            if(!lessonDO.isDeleted()){
                lessonBOList.add(convert.convertSimple(lessonDO));
            }
        }
        return lessonBOList;
    }

    @Override
    public LessonBO getLessonById(LessonRequest request) {
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(request.getLessonId());
        AssertUtil.assertNotNull(lessonDO, ExceptionResultCode.ILLEGAL_PARAMETERS,"查无此课程号对应的课程");
        return convert.convert(lessonDO, request.getUserId());
    }

    @Override
    public LessonPassageBO getPassageById(LessonRequest request) {
        LessonPassageDO passageDO = lessonPassageDORepo.findAllByPassageId(request.getPassageId());
        AssertUtil.assertNotNull(passageDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"查无该id对应的章节");
        return convert.convert(passageDO);
    }

    @Override
    public List<LessonPassageBO> getPassageByLessonId(LessonRequest request) {
        List<LessonPassageDO> passageDOList = lessonPassageDORepo.findAllByLessonIdAndDeleted(request.getLessonId(), false);
        return CollectionUtils.toStream(passageDOList)
                .filter(Objects::nonNull)
                .map(convert::convert)
                .toList();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchImportStudent(LessonRequest request) {
        //todo 鉴权
        LessonDO lessonDO = lessonDORepo.findAllByLessonId(request.getLessonId());
        AssertUtil.assertNotNull(lessonDO,ExceptionResultCode.ILLEGAL_PARAMETERS,"未找到该id对应的课程");
        //todo 为管理员开绿灯
        AssertUtil.assertEquals(lessonDO.getUserId(),request.getUserId(),ExceptionResultCode.FORBIDDEN,"您不是该课程的创立者，无法导入学生名单！");

        //todo 优化，使用更好的读取方式和格式
        InputStream excelStream=null;
        try {
            excelStream = request.getUserExcelFile().getInputStream();
        } catch (IOException e) {
            log.error("无法从上传的选课文件中打开文件流",e);
            throw new ZCMUException("无法从上传的选课文件中打开文件流");
        }

        //从excel中读取数据
        List<UserLessonExcelBO> userList=new ArrayList<>();
        EasyExcel.read(excelStream, UserLessonExcelBO.class,new EasyExcelListener<UserLessonExcelBO>(userList)).sheet().doRead();

        //获取已选该课程的学生id缓存
        List<LessonUserRelationDO> relationDOList = lessonUserRelationDORepo.findAllByLessonId(request.getLessonId());
        Set<Long> userIDS=new HashSet<>();
        relationDOList.forEach(
                lessonUserRelationDO -> {
                    userIDS.add(lessonUserRelationDO.getUserId());
                }
        );

        List<LessonUserRelationDO> saveList=new ArrayList<>() ;
        for (UserLessonExcelBO userLessonExcelBO : userList) {
            UserInfoDO userInfoDO = userInfoDORepo.findAllByStuIdAndRealName(
                    Long.parseLong(userLessonExcelBO.getStuId()), userLessonExcelBO.getRealName());
            AssertUtil.assertNotNull(userInfoDO,ExceptionResultCode.ILLEGAL_PARAMETERS
                    ,"未在数据库中找到名字为【"+userLessonExcelBO.getRealName()+"】,学号为【"+
                            userLessonExcelBO.getStuId()+"】的学生，请确保学生名单已经全部导入，本次批量导入选课学生名单未进行");
            if(userIDS.contains(userInfoDO.getUserId())) continue;
            saveList.add(LessonUserRelationDO.builder()
                    .relationId(IdUtil.getSnowflakeNextId())
                    .lessonId(request.getLessonId())
                    .userId(userInfoDO.getUserId())
                    .build());
        }
        lessonUserRelationDORepo.saveAll(saveList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //todo 检测XSS攻击
    public Integer updateLessonName(LessonRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //todo 检测XSS攻击
    public Integer updateLessonInfo(LessonRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer updateLessonPic(LessonRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    //todo 检测XSS攻击
    public Integer updatePassageLessonName(LessonRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deleteLesson(LessonRequest request) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Integer deletePassage(LessonRequest request) {
        return null;
    }
}
