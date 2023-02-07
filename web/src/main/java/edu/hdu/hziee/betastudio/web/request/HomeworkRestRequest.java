package edu.hdu.hziee.betastudio.web.request;

import edu.hdu.hziee.betastudio.web.aop.UserCheckedRequest;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class HomeworkRestRequest implements UserCheckedRequest {

    private Long homeworkId;

    private Long lessonId;

    private Long userId;

    private String info;

    private String name;

    //作业开始时间
    private Long start;

    //作业结束时间
    private Long end;

    //教师打分
    private Integer score;

    //提交的作业回答内容
    private String content;

    //作业提交id
    private Long submitHomeworkId;

    //资源id列表
    private List<Long> resourceList;

    //要删除的资源id列表
    private List<Long> deletedResourceList;
}
