package edu.hdu.hziee.betastudio.dao.comment.repo;

import edu.hdu.hziee.betastudio.dao.comment.model.CommentDO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentDORepo extends JpaRepository<CommentDO,Long> {

    CommentDO findAllByCommentId(Long commitId);

    List<CommentDO> findAllByThemeIdAndDeleted(Long themeId,boolean deleted);

    List<CommentDO> findAllByMasterIdAndDeleted(Long themeId,boolean deleted);

    /**
     * 备用查询语句，推荐使用
     * 使用递归查询，目的是通过数据库保存的前一条评论id找出完整评论链
     * @param commentId         主评论id
     */
    @Query(value = "with recursive zcmu_comment_tmp as " +
            "( " +
            "select * from zcmu_comment where comment_id = ?1 " +
            "union all " +
            "select zcmu_comment.* from zcmu_comment, zcmu_comment_tmp " +
            "where zcmu_comment.previous_comment_id = zcmu_comment_tmp.comment_id " +
            ") " +
            "select * from role_extend_temp;", nativeQuery = true)
    List<CommentDO> findCommentList(Long commentId);
}
