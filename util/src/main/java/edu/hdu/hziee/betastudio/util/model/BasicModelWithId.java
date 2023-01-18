package edu.hdu.hziee.betastudio.util.model;

import jakarta.persistence.*;
import lombok.Data;

/**
 * 为缺失主键的实体设计的基类
 */
@Data
@MappedSuperclass
public abstract class BasicModelWithId extends BasicModel{

    /**
     * 主键id
     */
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id",nullable = false,updatable = false,unique = true)
    private Long id;
}
