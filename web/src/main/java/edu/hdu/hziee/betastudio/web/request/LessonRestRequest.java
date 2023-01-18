package edu.hdu.hziee.betastudio.web.request;

import edu.hdu.hziee.betastudio.web.aop.UserCheckedRequest;
import lombok.Data;

import java.util.List;

@Data
public class LessonRestRequest implements UserCheckedRequest {

    private Long passageId;

    private Long lessonId;

    private Long userId;

    private String name;

    private String picUrl;

    //详细介绍
    private String info;

    private Long submitId;

    private List<Long> resourceList;
}
