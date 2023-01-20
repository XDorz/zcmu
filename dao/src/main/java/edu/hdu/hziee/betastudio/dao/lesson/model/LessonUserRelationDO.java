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
@org.hibernate.annotations.Table(appliesTo = "lesson_user",comment = "用户选课表")
@Table(name = "lesson_user",indexes = {
        @Index(name = "uk_lesson_id",columnList = "lesson_id",unique = false),
        @Index(name = "uk_user_id",columnList = "user_id",unique = false),
})
public class LessonUserRelationDO extends BasicModel {

    @Id
    @Column(name="relation_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '关系id'")
    private Long relationId;

    @Column(name="lesson_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '课程id'")
    private Long lessonId;

    @Column(name="user_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '选课人id'")
    private Long userId;

    @Tolerate
    public LessonUserRelationDO(){}
}
