package edu.hdu.hziee.betastudio.dao.resours.repo;

import edu.hdu.hziee.betastudio.dao.resours.model.ResoursDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResoursDORepo extends JpaRepository<ResoursDO,Long> {

    ResoursDO findAllByResourceId(Long resourceId);

    List<ResoursDO> findAllByBelongIdAndDeleted(Long belongId,boolean deleted);

    @Modifying
    @Query(value = "update zcmu_resource set url=?2 where resource_id=?1",nativeQuery = true)
    Integer updateUrl(Long resourceId,String url);

    @Modifying
    @Query(value = "update zcmu_resource set resource_name=?2 where resource_id=?1",nativeQuery = true)
    Integer updateName(Long resourceId,String resourceName);

    @Modifying
    @Query(value = "update zcmu_resource set belong_id=?2 where resource_id=?1",nativeQuery = true)
    Integer updateBelong(Long resourceId,Long belongId);

    @Modifying
    @Query(value = "update zcmu_resource set info=?2 where resource_id=?1",nativeQuery = true)
    Integer updateInfo(Long resourceId,String info);

    @Modifying
    @Query(value = "update zcmu_resource set pic_url=?2 where resource_id=?1",nativeQuery = true)
    Integer updatePic(Long resourceId,String picUrl);

    @Modifying
    @Query(value = "update zcmu_resource set deleted=?2 where resource_id=?1",nativeQuery = true)
    Integer deleteSrc(Long resourceId,boolean deleted);
}
