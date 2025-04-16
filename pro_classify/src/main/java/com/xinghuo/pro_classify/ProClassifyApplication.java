package com.xinghuo.pro_classify;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class ProClassifyApplication {
    public static void main(String[] args)  {
        SpringApplication.run(ProClassifyApplication.class, args);
    }
}
