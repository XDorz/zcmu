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
@org.hibernate.annotations.Table(appliesTo = "lesson",comment = "课程表")
@Table(name = "lesson",indexes = {
//        @Index(name = "uk_account",columnList = "account",unique = true)
})
public class LessonDO extends BasicModel {

    @Id
    @Column(name="lesson_id",updatable = false,nullable = false,unique = true,columnDefinition = "bigint(20) comment '课程id'")
    private Long lessonId;

    @Column(name="user_id",updatable = false,nullable = false,unique = false,columnDefinition = "bigint(20) comment '课程创建人id'")
    private Long userId;

    @Column(name="lesson_name",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(32) default '' comment '课程名'")
    private String lessonName;

    @Column(name="pic_url",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(128) default '' comment '课程封面图片'")
    private String picUrl;

    @Column(name="info",updatable = true,nullable = false,unique = false,columnDefinition = "varchar(128) default '' comment '课程介绍信息'")
    private String info;

    @Column(name="ext",updatable = true,nullable = true,unique = false,columnDefinition = "varchar(128) default '' comment '额外信息'")
    private String ext;

    @Column(name = "deleted",updatable = true,nullable = false,unique = false,columnDefinition = "bit(1) default false comment '是否已经删除'")
    private boolean deleted;

    @Tolerate
    public LessonDO(){}
}
