package com.srmist.academia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AcademiaScraperApplication {
    public static void main(String[] args) {
        SpringApplication.run(AcademiaScraperApplication.class, args);
    }
}
