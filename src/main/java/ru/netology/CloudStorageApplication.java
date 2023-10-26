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


    /**
     * Интерфейс CommandLineRunner
     * Это функциональный интерфейс, поэтому его можно использовать в качестве цели назначения для лямбда-выражения или ссылки на метод.
     * Интерфейс, используемый для указания того, что компонент должен запускаться , если он содержится в файле SpringApplication.
     * Несколько CommandLineRunner bean-компонентов могут быть определены в одном контексте приложения
     * и упорядочены с помощью Ordered интерфейса или @Order аннотации.
     *
     * CommandLineRunner - это функциональный интерфейс Spring Boot, который используется для запуска кода при запуске приложения.
     * Он находится в пакете org.springframework.boot.
     *
     * В процессе запуска после инициализации контекста Spring boot вызывает свой метод run() с аргументами командной строки, предоставленными приложению.
     *
     * Чтобы сообщить Spring Boot о нашем интерфейсе commandlineRunner, мы можем либо реализовать его и добавить аннотацию @Component над классом,
     * либо создать его bean-компонент с помощью @bean.
     */
//    @Bean
//    CommandLineRunner commandLineRunner(UserRepository users, PasswordEncoder encoder) {
//        return args -> users.save(new User("user", encoder.encode("password"), "USER"));
    //}
}