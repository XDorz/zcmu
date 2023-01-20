package edu.hdu.hziee.betastudio.util.customenum.basic;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "zcmu.constant")
@PropertySource("classpath:/config/application.yml")
public class ZCMUConstant {

    private String userPicUrl;
    private String lessonPicUrl;
    private String deletedPicUrl;
    private String authHead;
    private String managerAccount;
    private String defaultPassword;

    @PostConstruct
    private void init(){
        this.AUTH_HEAD=authHead;
        this.USER_PIC_URL=userPicUrl;
        this.LESSON_PIC_URL=lessonPicUrl;
        this.DEFAULT_PASSWORD=defaultPassword;
        this.DELETED_PIC_URL=deletedPicUrl;
        this.MANAGER_ACCOUNT=managerAccount;
    }

    public static String USER_PIC_URL;
    public static String LESSON_PIC_URL;
    public static String DELETED_PIC_URL;
    public static String AUTH_HEAD;
    public static String DEFAULT_PASSWORD;
    public static String MANAGER_ACCOUNT;
}
