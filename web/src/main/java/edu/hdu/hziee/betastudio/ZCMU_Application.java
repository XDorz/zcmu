package edu.hdu.hziee.betastudio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;


@EnableJpaAuditing
@EnableScheduling
@SpringBootApplication
public class ZCMU_Application {

    public static void main(String[] args) {
        SpringApplication.run(ZCMU_Application.class, args);
    }
}
