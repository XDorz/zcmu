package edu.hdu.hziee.betastudio.dao.perm.repo;

import edu.hdu.hziee.betastudio.dao.perm.model.PermDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermDORepo extends JpaRepository<PermDO,Long> {

    PermDO findAllByPermId(Long permId);

    List<PermDO> findAllByDeleted(boolean deleted);

    PermDO findAllByCodeName(String codeName);

    boolean existsByCodeName(String codeName);

    @Modifying
    @Query(value = "update zcmu_perm set deleted=?2 where perm_id=?1",nativeQuery = true)
    Integer deletePerm(Long permId,boolean deleted);

}
