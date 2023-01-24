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
@org.hibernate.annotations.Table(appliesTo = "zcmu_theme",comment = "主题表")
@Table(name = "zcmu_theme",indexes = {
        @Index(name = "uk_user_id",columnList = "user_id"),
        @Index(name = "uk_deleted",columnList = "deleted"),
})
public class ThemeDO extends BasicModel {

    @Id
    @Column(name="theme_id",updatable = false,nullable = true,unique = false,columnDefinition = "bigint(20) comment '主题id(帖子id)'")
    private Long themeId;

    @Column(name="user_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '发布人id'")
    private Long userId;

    @Column(name="theme_name",updatable = false,nullable = false,unique = false,columnDefinition = "varchar(32) comment '主题名称'")

    private String themeName;

    //一评论10热度，一浏览1热度
    @Column(name="hot",updatable = true,nullable = false,unique = false,columnDefinition = "int comment '热度'")
    private int hot;

    //评论量
    @Column(name="comment_num",updatable = true,nullable = false,unique = false,columnDefinition = "int comment '评论量'")
    private int commentNum;

    @Column(name = "deleted",updatable = true,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public ThemeDO(){}
}
