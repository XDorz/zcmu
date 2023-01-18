package edu.hdu.hziee.betastudio.dao.lesson.repo;

import edu.hdu.hziee.betastudio.dao.lesson.model.HomeworkDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HomeworkDORepo extends JpaRepository<HomeworkDO,Long> {

    HomeworkDO findAllByHomeworkId(Long homeworkId);

    List<HomeworkDO> findAllByLessonIdAndDeletedOrderByEndDesc(Long lessonId,boolean deleted);
}
