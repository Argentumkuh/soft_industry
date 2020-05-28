package com.soft_industry.demo;

import com.soft_industry.demo.model.Client;
import com.soft_industry.demo.repository.ClientRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.lang.NonNull;

@SpringBootApplication
@EnableJpaRepositories
@RequiredArgsConstructor
public class DemoApplication implements CommandLineRunner {

    @NonNull
    private ClientRepo clientRepo;

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Override
    public void run(String... args) {
        clientRepo.findByEmail("testAccount@test.com")
                .ifPresentOrElse(client -> {},
                () -> clientRepo.save(new Client("testAccount@test.com", "test_password")));
    }
}
