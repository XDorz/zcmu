package edu.hdu.hziee.betastudio.dao.lesson.repo;

import edu.hdu.hziee.betastudio.dao.lesson.model.LessonUserRelationDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonUserRelationDORepo extends JpaRepository<LessonUserRelationDO,Long> {

    List<LessonUserRelationDO> findAllByLessonId(Long lessonId);

    List<LessonUserRelationDO> findAllByUserId(Long userId);

    boolean existsByLessonIdAndUserId(Long lessonId,Long userId);
}
