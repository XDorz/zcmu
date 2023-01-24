package edu.hdu.hziee.betastudio.dao.comment.repo;

import edu.hdu.hziee.betastudio.dao.comment.model.ThemeDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeDORepo extends JpaRepository<ThemeDO,Long> {

    ThemeDO findAllByThemeId(Long themeId);

    List<ThemeDO> findAllByDeleted(boolean deleted);

    @Modifying
    @Query(value = "update zcmu_theme set hot=hot+?2 where theme_id=?1",nativeQuery = true)
    Integer increaseHot(Long themeId,int increasedNum);

    @Modifying
    @Query(value = "update zcmu_theme set comment_num=comment_num+?2 where theme_id=?1",nativeQuery = true)
    Integer increaseCommentNum(Long themeId,int increasedNum);

    @Modifying
    @Query(value = "update zcmu_theme set deleted=?2 where theme_id=?1",nativeQuery = true)
    Integer deleteTheme(Long themeId,boolean deleted);
}
