package edu.hdu.hziee.betastudio.dao.user.repo;

import edu.hdu.hziee.betastudio.dao.user.model.UserInfoDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserInfoDORepo extends JpaRepository<UserInfoDO,Long> {

    UserInfoDO findAllByUserId(Long userId);

    UserInfoDO findAllByStuIdAndRealName(Long stuId,String realName);

    @Modifying
    @Query(value = "update user_info set pic_url=?2 where user_id=?1",nativeQuery = true)
    Integer updatePic(Long userId,String picUrl);

    @Modifying
    @Query(value = "update user_info set user_name=?2 where user_id=?1",nativeQuery = true)
    Integer updateUserName(Long userId,String userName);
}
