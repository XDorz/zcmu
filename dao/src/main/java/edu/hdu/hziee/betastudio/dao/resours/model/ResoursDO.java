package edu.hdu.hziee.betastudio.dao.resours.model;

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
@org.hibernate.annotations.Table(appliesTo = "zcmu_resource", comment = "资源表")
@Table(name = "zcmu_resource", indexes = {
        @Index(name = "uk_belong_id", columnList = "belong_id", unique = false),
        @Index(name = "uk_deleted", columnList = "deleted", unique = false),
        @Index(name = "uk_user_id", columnList = "user_id", unique = false),
})
public class ResoursDO {

    @Id
    @Column(name = "resource_id", updatable = false, nullable = false, unique = true, columnDefinition = "bigint(20) comment '资源id'")
    private Long resourceId;

    @Column(name = "belong_id", updatable = true, nullable = true, unique = false, columnDefinition = "bigint(20) comment '所属id'")
    private Long belongId;

    @Column(name = "user_id", updatable = false, nullable = false, unique = false, columnDefinition = "bigint(20) comment '资源上传者id'")
    private Long userId;

    @Column(name = "url", updatable = false, nullable = true, unique = true, columnDefinition = "varchar(128) comment '资源链接(OSS)'")
    private String url;

    @Column(name = "resource_name", updatable = true, nullable = true, unique = false, columnDefinition = "varchar(512) comment '资源名称'")
    private String name;

    @Column(name = "info", updatable = true, nullable = true, unique = false, columnDefinition = "varchar(512) comment '资源描述'")
    private String info;

    @Column(name = "pic_url", updatable = true, nullable = true, unique = false, columnDefinition = "varchar(128) comment '资源封面图片链接(OSS)'")
    private String picUrl;

    @Column(name = "ext", updatable = true, nullable = true, unique = false, columnDefinition = "varchar(128) default '' comment '额外信息'")
    private String ext;

    @Column(name = "deleted", updatable = true, nullable = false, unique = false, columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public ResoursDO() {
    }
}
