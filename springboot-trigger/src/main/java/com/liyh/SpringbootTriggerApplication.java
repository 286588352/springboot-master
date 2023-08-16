package com.liyh;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpringbootTriggerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringbootTriggerApplication.class, args);
    }

}
