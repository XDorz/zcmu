package edu.hdu.hziee.betastudio.dao.lesson.repo;

import edu.hdu.hziee.betastudio.dao.lesson.model.LessonPassageDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonPassageDORepo extends JpaRepository<LessonPassageDO,Long> {

    LessonPassageDO findAllByPassageId(Long passageId);

    List<LessonPassageDO> findAllByLessonIdAndDeleted(Long lessonId,boolean deleted);
}
