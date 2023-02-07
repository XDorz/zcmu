package edu.hdu.hziee.betastudio.dao.lesson.repo;

import edu.hdu.hziee.betastudio.dao.lesson.model.HomeworkDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface HomeworkDORepo extends JpaRepository<HomeworkDO,Long> {

    HomeworkDO findAllByHomeworkId(Long homeworkId);

    List<HomeworkDO> findAllByLessonIdAndDeletedOrderByEndDesc(Long lessonId,boolean deleted);

    @Modifying
    @Query(value = "update lesson_homework set homework_name=?2 where homework_id=?1",nativeQuery = true)
    Integer updateHomeworkName(Long homeworkId,String homeworkName);

    @Modifying
    @Query(value = "update lesson_homework set info=?2 where homework_id=?1",nativeQuery = true)
    Integer updateHomeworkInfo(Long homeworkId,String info);

    @Modifying
    @Query(value = "update lesson_homework set start=?2,end=?3 where homework_id=?1",nativeQuery = true)
    Integer updateHomeworkTime(Long homeworkId, Date start, Date end);

    @Modifying
    @Query(value = "update lesson_homework set deleted=?2 where homework_id=?1",nativeQuery = true)
    Integer deleteHomework(Long homeworkId,boolean deleted);
}
