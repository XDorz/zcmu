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
@org.hibernate.annotations.Table(appliesTo = "zcmu_perm",comment = "权限表")
@Table(name = "zcmu_perm",indexes = {
        @Index(name = "uk_perm_code",columnList = "perm_code",unique = true)
})
public class PermDO extends BasicModel {

    @Id
    @Column(name="perm_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '权限id'")
    private Long permId;

    @Column(name="perm_name",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(16) default '' comment '权限名称'")
    private String permName;

    @Column(name="perm_code",updatable = true,nullable = false,unique = true,columnDefinition = "varchar(32) default '' comment '权限code'")
    private String codeName;

    @Column(name = "deleted",updatable = true,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public PermDO(){}
}
