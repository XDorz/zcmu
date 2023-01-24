package edu.hdu.hziee.betastudio.dao.perm.model;

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
@org.hibernate.annotations.Table(appliesTo = "perm_user_relation",comment = "权限-用户关系表")
@Table(name = "perm_user_relation",indexes = {
        @Index(name = "uk_perm_id",columnList = "perm_id",unique = false),
        @Index(name = "uk_user_id",columnList = "user_id",unique = false),
})
public class PermUserRelationDO extends BasicModel {

    @Id
    @Column(name="relation_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '关系id'")
    private Long relationId;

    @Column(name="perm_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '权限id'")
    private Long permId;

    @Column(name="user_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;

    @Tolerate
    public PermUserRelationDO(){}
}
