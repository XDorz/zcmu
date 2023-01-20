package edu.hdu.hziee.betastudio.dao.perm.repo;

import edu.hdu.hziee.betastudio.dao.perm.model.PermUserRelationDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermUserRelationDORepo extends JpaRepository<PermUserRelationDO,Long> {

    List<PermUserRelationDO> findAllByUserId(Long userId);

    List<PermUserRelationDO> findAllByPermId(Long permId);

    boolean existsByUserIdAndPermId(Long userId,Long permId);

    @Modifying
    Integer deleteAllByPermId(Long permId);

    @Modifying
    Integer deleteAllByUserId(Long userId);

    @Modifying
    @Query(value = "delete from perm_user_relation where userId=?1 and perm_id in (?2)",nativeQuery = true)
    Integer takeBackPerms(Long userId,List<Long> permIds);
}
