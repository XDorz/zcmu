package edu.hdu.hziee.betastudio.dao.lesson.model;

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
@org.hibernate.annotations.Table(appliesTo = "submit_homework",comment = "已提交作业表")
@Table(name = "submit_homework",indexes = {
        @Index(name = "uk_homework_id",columnList = "homework_id",unique = false),
        @Index(name = "uk_user_id",columnList = "user_id",unique = false)
})
public class SubmitHomeworkDO extends BasicModel {

    @Id
    @Column(name="submit_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '作业提交id'")
    private Long submitId;

    @Column(name="homework_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '关联的作业id'")
    private Long homeworkId;

    @Column(name="user_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '作业提交人id'")
    private Long userId;

    @Column(name="content",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(2048) comment '提交的内容'")
    private String content;

    @Column(name="score",updatable = true,nullable = true,unique = false,columnDefinition = "int(3) comment '教师给分'")
    private Integer score;

    @Tolerate
    public SubmitHomeworkDO(){}
}
