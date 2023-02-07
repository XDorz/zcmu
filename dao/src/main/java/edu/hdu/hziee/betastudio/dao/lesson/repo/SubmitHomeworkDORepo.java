package edu.hdu.hziee.betastudio.dao.lesson.repo;

import edu.hdu.hziee.betastudio.dao.lesson.model.SubmitHomeworkDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SubmitHomeworkDORepo extends JpaRepository<SubmitHomeworkDO,Long> {

    boolean existsByHomeworkIdAndUserId(Long homeworkId,Long userId);

    SubmitHomeworkDO findAllBySubmitId(Long submitId);

    List<SubmitHomeworkDO> findAllByHomeworkId(Long homeworkId);

    SubmitHomeworkDO findAllByHomeworkIdAndUserId(Long homeworkId,Long userId);

    @Modifying
    @Query(value = "update submit_homework set score=?2 where submit_id=?1",nativeQuery = true)
    Integer scoreHomework(Long submitHomeworkId,int score);

    @Modifying
    @Query(value = "update submit_homework set content=?2 where submit_id=?1",nativeQuery = true)
    Integer updateContent(Long submitHomeworkId,String content);
}
