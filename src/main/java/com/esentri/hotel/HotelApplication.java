package com.esentri.hotel;

import io.domainlifecycles.autoconfig.annotation.EnableDlc;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableDlc(dlcMirrorBasePackages = "com.esentri.hotel", transactionCacheEnabled = true)
public class HotelApplication {

    public static void main(String[] args) {
        SpringApplication.run(HotelApplication.class, args);
    }
}
