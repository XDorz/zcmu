package edu.hdu.hziee.betastudio.util.model;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.util.Date;

/**
 * 仓储实体基础抽象类
 */
@Data
@MappedSuperclass
public abstract class BasicModel {

    /**
     * 数据创建时间
     */
    @CreatedDate
    @Column(name="gmt_creat",updatable = false,nullable = false)
    private Date creatDate;

    /**
     * 数据最后更改时间
     */
    @LastModifiedDate
    @Column(name="gmt_last_modified",nullable = false)
    private Date lastModifyDate;
}
