package ru.netology;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.netology.entities.User;
import ru.netology.repositories.UserRepository;

@SpringBootApplication
public class CloudStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(CloudStorageApplication.class, args);
    }

    @Bean
    CommandLineRunner commandLineRunner(UserRepository users, PasswordEncoder encoder) {
        return args -> users.save(new User("user", encoder.encode("password"), "USER"));
    }
}