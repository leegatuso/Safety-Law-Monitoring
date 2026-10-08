package com.company.safetylaw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SafetyLawApplication {
    public static void main(String[] args) {
        SpringApplication.run(SafetyLawApplication.class, args);
    }
}
