package edu.hdu.hziee.betastudio.dao.user.model;

import edu.hdu.hziee.betastudio.util.model.BasicModel;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Tolerate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.util.Date;

@Data
@Entity
@Builder
@ToString
@EntityListeners(AuditingEntityListener.class)
@org.hibernate.annotations.Table(appliesTo = "zcmu_user",comment = "用户表")
@Table(name = "zcmu_user",indexes = {
        @Index(name = "uk_account",columnList = "account",unique = true)
})
public class UserDO extends BasicModel {

    @Id
    @Column(name="user_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;

    /**
     * 用户账户
     */
    @Column(name="account",nullable = false,columnDefinition = "varchar(32) default '' comment '用户账户'")
    private String account;

    /**
     * 密码
     */
    @Column(name="password",nullable = false,columnDefinition = "varchar(32) default '' comment '用户密码'")
    private String password;

    /**
     * 盐
     */
    @Column(name="salt",nullable = false,columnDefinition = "varchar(36) default '' comment '盐'")
    private String salt;

    /**
     * 最后一次登录时间
     */
    @Column(name = "last_login_date",columnDefinition = "datetime comment '最后一次登录时间'")
    private Date lastLoginDate;

    /**
     * 最后一次登录 ip
     */
    @Column(name = "last_login_ip",columnDefinition = "varchar(15) default '' comment '最后一次登录ip'")
    private String lastLoginIp;

    @Tolerate
    public UserDO(){}
}
