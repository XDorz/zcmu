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
@org.hibernate.annotations.Table(appliesTo = "lesson_passage",comment = "课程章节表")
@Table(name = "lesson_passage",indexes = {
        @Index(name = "uk_lesson_id",columnList = "lesson_id",unique = false),
        @Index(name = "uk_deleted",columnList = "deleted",unique = false),
})
public class LessonPassageDO extends BasicModel {

    @Id
    @Column(name="passage_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '章节id'")
    private Long passageId;

    @Column(name="lesson_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '关联的课程id'")
    private Long lessonId;

    @Column(name="passage_name",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(32) comment '作业名'")
    private String name;

    @Column(name = "deleted",updatable = true,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public LessonPassageDO(){}
}
