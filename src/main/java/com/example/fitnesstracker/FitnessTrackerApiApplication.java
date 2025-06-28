package com.example.fitnesstracker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;

@SpringBootApplication
@EnableMethodSecurity()
public class FitnessTrackerApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(FitnessTrackerApiApplication.class, args);
    }

}
