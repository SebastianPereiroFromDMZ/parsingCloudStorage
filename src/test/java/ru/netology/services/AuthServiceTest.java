package ru.netology.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import ru.netology.dto.AuthRequest;
import ru.netology.security.JwtTokenUtils;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.given;

//Другая полезная аннотация — @ExtendWith. Скорее всего ты будешь встречать ее очень часто, так что рассмотрим ее подробнее.
//
//JUnit — это мощный фреймворк, который позволяет писать различные плагины (расширения) для гибкой настройки своей работы.
//Некоторые расширения могут собирать статистику о тестах, другие — эмулировать файловую систему в памяти, третьи — эмулировать работу внутри веб-сервера,
//и так далее.
//
//Если твой код работает внутри какого-нибудь фреймворка (например Spring),
//то почти всегда этот фреймворк управляет созданием и настройкой объектов твоего кода. Поэтому без специального тестового плагина не обойтись.

//SpringExtension создает тестовый вариант фреймворка Spring, а MockitoExtension позволяет создавать фейковые объекты.
@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenUtils jwtTokenUtils;
    private final String USERNAME = "admin";
    private final String PASSWORD = "admin";
    private final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
    private final String token = UUID.randomUUID().toString();
    private final AuthRequest authRequest = new AuthRequest(USERNAME, PASSWORD);

    @Test
    void loginUserTest() {
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        given(jwtTokenUtils.generateToken(authentication)).willReturn(token);
        assertEquals(token, authService.loginUser(authRequest));
    }
}