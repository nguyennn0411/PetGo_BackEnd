package com.example.petgo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PetgoApplication {

    public static void main(String[] args) {
        SpringApplication.run(PetgoApplication.class, args);
    }

}
