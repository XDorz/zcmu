package edu.hdu.hziee.betastudio.business.lesson.request;

import edu.hdu.hziee.betastudio.business.aop.PermRequest;
import lombok.Builder;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@Builder
public class LessonRequest extends PermRequest {

    private Long passageId;

    private Long lessonId;

    private Long userId;

    private String name;

    private String picUrl;

    //详细介绍
    private String info;

    private Long submitId;

    private MultipartFile picFile;

    //学习选课名单
    private MultipartFile userExcelFile;

    private List<Long> resourceList;
}
