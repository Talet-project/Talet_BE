package com.talet.talet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class TaletApplication {

    public static void main(String[] args) {
        // JVM 시간을 한국 시간으로 변경
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Seoul"));
        SpringApplication.run(TaletApplication.class, args);
    }

}
