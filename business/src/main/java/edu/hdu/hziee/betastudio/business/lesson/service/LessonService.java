package edu.hdu.hziee.betastudio.business.lesson.service;

import edu.hdu.hziee.betastudio.business.lesson.model.LessonBO;
import edu.hdu.hziee.betastudio.business.lesson.model.LessonPassageBO;
import edu.hdu.hziee.betastudio.business.lesson.model.SimpleLessonBO;
import edu.hdu.hziee.betastudio.business.lesson.request.LessonRequest;

import java.util.List;

public interface LessonService {

    /**
     * 创建课程
     */
    LessonBO createLesson(LessonRequest request);

    /**
     * 创建课程章节
     */
    LessonPassageBO createPassageLesson(LessonRequest request);

    /**
     * 获取学生所选所有未删除的课程
     */
    List<SimpleLessonBO> getAllChooseLesson(LessonRequest request);

    /**
     * 教师获取所有自己创建的课程
     */
    List<SimpleLessonBO> getAllCreateLesson(LessonRequest request);

    /**
     * 通过课程id获取最详细课程信息
     */
    LessonBO getLessonById(LessonRequest request);

    /**
     * 通过章节id获取最详细章节信息
     */
    LessonPassageBO getPassageById(LessonRequest request);

    /**
     * 查看该课程下所有的章节
     */
    List<LessonPassageBO> getPassageByLessonId(LessonRequest request);

    void batchImportStudent(LessonRequest request);

    Integer updateLessonName(LessonRequest request);

    Integer updateLessonInfo(LessonRequest request);

    Integer updateLessonPic(LessonRequest request);

    Integer updatePassageLessonName(LessonRequest request);

    Integer deleteLesson(LessonRequest request);

    Integer deletePassage(LessonRequest request);
}
