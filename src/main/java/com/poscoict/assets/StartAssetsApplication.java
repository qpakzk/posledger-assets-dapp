package com.poscoict.assets;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@ServletComponentScan
@SpringBootApplication
public class StartAssetsApplication {

    public static void main(String[] args) {
        SpringApplication.run(StartAssetsApplication.class, args);
    }
}
