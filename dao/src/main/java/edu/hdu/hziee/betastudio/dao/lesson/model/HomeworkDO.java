package edu.hdu.hziee.betastudio.dao.lesson.model;

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
@org.hibernate.annotations.Table(appliesTo = "lesson_homework",comment = "课程作业表")
@Table(name = "lesson_homework",indexes = {
        @Index(name = "uk_lesson_id",columnList = "lesson_id",unique = false)
})
public class HomeworkDO extends BasicModel {

    @Id
    @Column(name="homework_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '作业id'")
    private Long homeworkId;

    @Column(name="lesson_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '课程id'")
    private Long lessonId;

    @Column(name="info",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(1024) comment '作业详情'")
    private String info;

    @Column(name="name",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(32) comment '作业名'")
    private String name;

    @Column(name="start",updatable = true,nullable = false,unique = false,columnDefinition = "datetime comment '作业开始时间'")
    private Date start;

    @Column(name="end",updatable = true,nullable = false,unique = false,columnDefinition = "datetime comment '作业结束时间'")
    private Date end;

    @Column(name="ext",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(128) default '' comment '额外信息'")
    private String ext;

    @Column(name = "deleted",updatable = true,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public HomeworkDO(){}
}
