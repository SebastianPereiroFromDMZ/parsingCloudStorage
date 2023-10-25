package ru.netology.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netology.dto.AuthRequest;
import ru.netology.dto.AuthResponse;
import ru.netology.services.AuthService;

@RestController
@RequestMapping("/")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        String token = authService.loginUser(authRequest);
        return token != null ? new ResponseEntity<>(new AuthResponse(token), HttpStatus.OK) :
                new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
    }

    @GetMapping("login")
    public ResponseEntity<?> login() {
        return ResponseEntity.ok(HttpStatus.OK);
    }

    @PostMapping("/logout")
    //@RequestHeader: Аннотация, указывающая, что параметр метода должен быть привязан к заголовку веб-запроса.
    //Поддерживается для аннотированных методов-обработчиков в Spring MVC и Spring WebFlux.
    //Если параметр метода — Map<String, String>, MultiValueMap<String, String> или HttpHeaders, то карта заполняется всеми именами и значениями заголовков.
    public ResponseEntity<?> logout(@RequestHeader("auth-token") String token) {
        authService.logoutUser(token);
        return ResponseEntity.ok(HttpStatus.OK);
    }
}