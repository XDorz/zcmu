package edu.hdu.hziee.betastudio.dao.lesson.repo;

import edu.hdu.hziee.betastudio.dao.lesson.model.LessonDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonDORepo extends JpaRepository<LessonDO,Long> {

    LessonDO findAllByLessonId(Long lessonId);

    List<LessonDO> findAllByUserId(Long userId);

    @Modifying
    @Query(value = "update lesson set lesson_name=?2 where lesson_id=?1",nativeQuery = true)
    Integer updateLessonName(Long lessonId,String lessonName);

    @Modifying
    @Query(value = "update lesson set pic_url=?2 where lesson_id=?1",nativeQuery = true)
    Integer updatePic(Long lessonId,String picUrl);

    @Modifying
    @Query(value = "update lesson set info=?2 where lesson_id=?1",nativeQuery = true)
    Integer updateInfo(Long lessonId,String info);

    @Modifying
    @Query(value = "update lesson set deleted=?2 where lesson_id=?1",nativeQuery = true)
    Integer deleteLesson(Long lessonId,boolean deleted);
}
