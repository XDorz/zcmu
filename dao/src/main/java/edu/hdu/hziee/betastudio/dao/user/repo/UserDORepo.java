package edu.hdu.hziee.betastudio.dao.user.repo;

import edu.hdu.hziee.betastudio.dao.user.model.UserDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface UserDORepo extends JpaRepository<UserDO,Long> {

    UserDO findAllByAccount(String account);

    @Modifying
    @Query(value = "update zcmu_user set last_login_date=?2,last_login_ip=?3 where user_id=?1",nativeQuery = true)
    Integer login(Long userId, Date loginDate,String loginIp);

    @Modifying
    @Query(value = "update zcmu_user set salt=?3,password=?2 where user_id=?1",nativeQuery = true)
    Integer updatePassword(Long userId,String password,String salt);
}
