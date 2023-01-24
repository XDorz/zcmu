package edu.hdu.hziee.betastudio.dao.comment.model;

import edu.hdu.hziee.betastudio.util.model.BasicModel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Data
@Entity
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@org.hibernate.annotations.Table(appliesTo = "zcmu_comment",comment = "评论表")
@Table(name = "zcmu_comment",indexes = {
        @Index(name = "uk_theme_id",columnList = "theme_id"),
        @Index(name = "uk_user_id",columnList = "user_id"),
        @Index(name = "uk_master_id",columnList = "master_id"),
        @Index(name = "uk_deleted",columnList = "deleted"),
})
public class CommentDO extends BasicModel {

    /**
     * 如果是帖子主内容则该id为themeId，themeId置为空
     */
    @Id
    @Column(name="comment_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '评论id'")
    private Long commentId;

    @Column(name="theme_id",updatable = false,nullable = true,unique = false,columnDefinition = "bigint(20) comment '所属主题id(帖子id)'")
    private Long themeId;

    @Column(name="user_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '评论人id'")
    private Long userId;

    @Column(name="previous_comment_id",updatable = true,nullable = true,unique = false,columnDefinition = "bigint(20) comment '前一个评论的id'")
    private Long previousCommentId;

    @Column(name="master_id",updatable = false,nullable = true,unique = false,columnDefinition = "bigint(20) comment '父评论id'")
    private Long masterId;

    @Column(name="content",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(2048) comment '评论内容'")
    private String content;

    @Column(name="ip_addr",updatable = false,nullable = false,unique = false,columnDefinition = "varchar(15) comment 'ip地址'")
    private String ipAddr;

    @Column(name="client_type",updatable = false,nullable = false,unique = false,columnDefinition = "varchar(2048) comment '客户端类型'")
    private String client_type;

    @Column(name = "teacher",updatable = false,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否是教师评论'")
    private boolean teacher;

    @Column(name = "deleted",updatable = true,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public CommentDO(){}
}
