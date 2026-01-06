package com.example.demo;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

@SpringBootApplication
public class DemoApplication {

    public static void main(String[] args) {
        new SpringApplicationBuilder(DemoApplication.class)
                .web(WebApplicationType.NONE) // spring-boot-starter-web을 의존해도 웹 서버를 띄우지 않음
                .bannerMode(Banner.Mode.OFF) // 배너 메시지 비활성화
                .run(args);
    }

}
