package com.esentri.hotel;

import io.domainlifecycles.autoconfig.annotation.EnableDlc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableDlc(dlcMirrorBasePackages = "com.esentri.hotel")
@SpringBootApplication
public class HotelverwaltungApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelverwaltungApplication.class, args);
    }
}
