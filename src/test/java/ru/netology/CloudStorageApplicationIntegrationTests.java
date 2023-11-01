package ru.netology;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.netology.dto.AuthRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

//По умолчанию аннотация @SpringBootTest не запускает сервер, а вместо этого создает имитационное окружение для тестирования конечных веб-точек.
//В Spring MVC можно запрашивать конечные веб-точки с помощью MockMvc или WebTestClient
@SpringBootTest
//@AutoConfigureMockMvc
//Эта аннотация нужна для того, чтобы появилась возможность внедрить в тестовый класс бин MockMvc
@AutoConfigureMockMvc
class CloudStorageApplicationIntegrationTests {
    @Autowired
    //Класс MockMvc предназначен для тестирования контроллеров. Он позволяет тестировать контроллеры без запуска http-сервера.
    //То есть при выполнении тестов сетевое соединение не создается.
    //С MockMvc можно писать как интеграционные тесты, так и unit-тесты
    private MockMvc mvc;
    @Autowired
    //ObjectMapper
    //Этот класс преобразовывает объект в JSON-строку. Он нужен, так как мы тестируем REST API, MockMvc самостоятельно это преобразование не делает.
    private ObjectMapper objectMapper;
    private final String LOGIN_PATH = "/login";
    private final String LOGOUT_PATH = "/logout";
    private final String LOGIN = "user";
    private final String BAD_LOGIN = "login";
    private final String PASSWORD = "password";

    @Test
    void loginUserUnauthenticated() throws Exception {
        AuthRequest authRequest = new AuthRequest(BAD_LOGIN, PASSWORD);
        //создаем Post-запрос по адресу в LOGIN_PATH, предварительно преобразовав в JSON-строку.
        mvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        //writeValueAsString Метод, который можно использовать для сериализации любого значения Java в виде строки.
                        //Функционально эквивалентно вызову writeValue (Writer, Object) с StringWriter и построению String, но более эффективно.
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginUserAuthenticated() throws Exception {
        AuthRequest authRequest = new AuthRequest(LOGIN, PASSWORD);
        mvc.perform(post(LOGIN_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void logoutUserTest() throws Exception {
        AuthRequest authRequest = new AuthRequest(LOGIN, PASSWORD);
        mvc.perform(post(LOGOUT_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(authRequest)))
                .andExpect(status().is3xxRedirection());
    }
}