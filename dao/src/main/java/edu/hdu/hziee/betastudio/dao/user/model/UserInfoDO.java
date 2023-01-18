package edu.hdu.hziee.betastudio.dao.user.model;

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
@org.hibernate.annotations.Table(appliesTo = "user_info",comment = "用户信息表")
@Table(name = "user_info",indexes = {
        @Index(name = "uk_stu_id",columnList = "stu_id",unique = true),
        @Index(name = "uk_real_name",columnList = "real_name",unique = true),
        @Index(name = "uk_deleted",columnList = "deleted"),
})
public class UserInfoDO extends BasicModel {

    @Id
    @Column(name="user_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '用户id'")
    private Long userId;

    @Column(name="stu_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(15) comment '学号'")
    private Long stuId;

    @Column(name="email",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(64) default '未知' comment '电子邮箱'")
    private String email;

    @Column(name="real_name",updatable = false,nullable = false,unique = false,columnDefinition = "varchar(6) default '未知' comment '真实姓名'")
    private String realName;

    @Column(name="sex",updatable = false,nullable = true,unique = false,columnDefinition = "varchar(1) default '女' comment '性别'")
    private String sex;

    @Column(name="collage",updatable = false,nullable = true,unique = false,columnDefinition = "varchar(16) default '护理学院' comment '学院'")
    private String collage;

    @Column(name="grade",updatable = false,nullable = true,unique = false,columnDefinition = "int(4) default 2023 comment '年级'")
    private Integer grade;

    @Column(name="major",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(16) default '护理学' comment '专业'")
    private String major;

    @Column(name="class",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(24) default '未知' comment '班级'")
    private String userClass;

    @Column(name="stu_mark",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(24) default '浙江中医药大学' comment '学生标记'")
    private String stuMark;

    @Column(name="minor_mark",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(16) default '×' comment '辅修标记'")
    private String minorMark;

    @Column(name="repair",updatable = true,nullable = true,unique = false,columnDefinition = "bit(1) default false comment '是否重修'")
    private boolean repair;

    @Column(name="patch",updatable = true,nullable = true,unique = false,columnDefinition = "bit(1) default false comment '是否补修'")
    private boolean patch;

    @Column(name="self_study",updatable = true,nullable = true,unique = false,columnDefinition = "bit(1) default false comment '是否自修'")
    private boolean selfStudy;

    @Column(name="user_name",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(16) comment '用户昵称'")
    private String userName;

    @Column(name="pic_url",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(128) comment '用户头像链接(OSS)'")
    private String picUrl;

    @Column(name="ext",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(124) default '' comment '额外信息'")
    private String ext;

    @Column(name = "deleted",updatable = true,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public UserInfoDO(){}
}
