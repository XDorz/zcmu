package edu.hdu.hziee.betastudio.dao.lesson.repo;

import edu.hdu.hziee.betastudio.dao.lesson.model.LessonPassageDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonPassageDORepo extends JpaRepository<LessonPassageDO,Long> {

    LessonPassageDO findAllByPassageId(Long passageId);

    List<LessonPassageDO> findAllByLessonIdAndDeleted(Long lessonId,boolean deleted);

    @Modifying
    @Query(value = "update lesson_passage set passage_name=?2 where passage_id=?1",nativeQuery = true)
    Integer updatePassageName(Long lessonId,String passageName);

    @Modifying
    @Query(value = "update lesson_passage set deleted=?2 where passage_id=?1",nativeQuery = true)
    Integer deletePassage(Long lessonId,boolean deleted);
}
