package edu.hdu.hziee.betastudio.dao.perm.repo;

import edu.hdu.hziee.betastudio.dao.perm.model.PermDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermDORepo extends JpaRepository<PermDO,Long> {

    PermDO findAllByPermId(Long permId);

    PermDO findAllByCodeName(String codeName);

    boolean existsByCodeName(String codeName);

}
