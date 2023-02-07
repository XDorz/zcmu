package edu.hdu.hziee.betastudio.business.lesson.service;

import edu.hdu.hziee.betastudio.business.lesson.model.HomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SubmitHomeworkBO;
import edu.hdu.hziee.betastudio.business.lesson.request.HomeworkRequest;
import edu.hdu.hziee.betastudio.business.user.model.AppUserInfoBO;
import edu.hdu.hziee.betastudio.util.customenum.HomeworkStatusEnum;
import io.swagger.models.auth.In;

import java.util.List;

public interface HomeworkService {

    //学生获取一门课程所有作业【不对外开放，仅作为内部方法实现接口】
    List<HomeworkBO> getAllLessonHomework(Long lessonId);

    //学生获取该门课程的所有作业
    List<HomeworkBO> getAllLessonHomework(HomeworkRequest request );

    //教师获取该门课程所有提交的作业
    List<SubmitHomeworkBO> getAllSubHomework(HomeworkRequest request);

    HomeworkBO createHomework(HomeworkRequest request);

    SubmitHomeworkBO createSubmitHomework(HomeworkRequest request);

    //学生获取自己提交的一项作业
    SubmitHomeworkBO getSelfSubmitHomework(HomeworkRequest request);

    //查看未提交作业学生列表
    List<AppUserInfoBO> getUnSubmitUserInfo(HomeworkRequest request);
    //教师打分
    void scoreHomework(HomeworkRequest request);

    //获取作业显示状态【不对外开放，仅作为内部方法实现接口】
    HomeworkStatusEnum getHomeworkStatus(Long homeworkId,Long userId);

    Integer updateHomeworkName(HomeworkRequest request);

    Integer updateHomeworkInfo(HomeworkRequest request);

    Integer updateHomeworkTime(HomeworkRequest request);

    Integer deleteHomework(HomeworkRequest request);

    Integer updateSubHomeworkContent(HomeworkRequest request);

    void updateSubHomeworkResource(HomeworkRequest request);
}
