package edu.hdu.hziee.betastudio.dao.comment.repo;

import edu.hdu.hziee.betastudio.dao.comment.model.ThemeSubscribeDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ThemeSubscribeDORepo extends JpaRepository<ThemeSubscribeDO,Long> {

    List<ThemeSubscribeDO> findByThemeId(Long themeId);

    List<ThemeSubscribeDO> findByUserId(Long userId);

    boolean existsByUserIdAndThemeId(Long userId,Long themeId);

    int countAllByThemeId(Long themeId);

    @Modifying
    Integer deleteByUserIdAndThemeId(Long userId,Long themeId);

    @Modifying
    Integer deleteAllByThemeId(Long themeId);
}
