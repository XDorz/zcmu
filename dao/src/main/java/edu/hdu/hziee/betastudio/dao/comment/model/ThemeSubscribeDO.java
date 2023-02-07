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
@org.hibernate.annotations.Table(appliesTo = "theme_subscribe",comment = "主题订阅表")
@Table(name = "theme_subscribe",indexes = {
        @Index(name = "uk_user_id",columnList = "user_id"),
        @Index(name = "uk_theme_id",columnList = "theme_id"),
})
public class ThemeSubscribeDO extends BasicModel {

    @Id
    @Column(name="subscribe_id",updatable = false,nullable = true,unique = false,columnDefinition = "bigint(20) comment '订阅id'")
    private Long subscribeId;

    @Column(name="theme_id",updatable = false,nullable = true,unique = false,columnDefinition = "bigint(20) comment '订阅的主题id(帖子id)'")
    private Long themeId;

    @Column(name="user_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '订阅人id'")
    private Long userId;

    @Tolerate
    public ThemeSubscribeDO(){};
}
