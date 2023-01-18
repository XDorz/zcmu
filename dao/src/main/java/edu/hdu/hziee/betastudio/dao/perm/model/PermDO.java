package edu.hdu.hziee.betastudio.dao.perm.model;

import edu.hdu.hziee.betastudio.util.model.BasicModel;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Table;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

//@Data
//@Entity
//@Builder
//@ToString
//@EntityListeners(AuditingEntityListener.class)
//@org.hibernate.annotations.Table(appliesTo = "zcmu_perm",comment = "权限表")
//@Table(name = "zcmu_perm",indexes = {
////        @Index(name = "uk_account",columnList = "account",unique = true)
//})
//public class PermDO extends BasicModel {
//
//    @Tolerate
//    public PermDO(){}
//}
