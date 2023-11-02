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
    //@Mock
    //Аннотация @Mock используется для создания и внедрения макета или стаба (mock object).
    //Макет — это объект, который имитирует реальное поведение объекта, но в упрощенной или контролируемой форме. Это очень полезно при тестировании,
    //поскольку позволяет изолировать код, который тестируется, от остальной системы.
    //
    //Пример использования @Mock:
    //@Mock
    //List mockedList;
    //В этом примере создается макет объекта типа List. Этот макет можно использовать в тестах, чтобы имитировать реальное поведение списка.
    //
    //@InjectMocks
    //Аннотация @InjectMocks используется для создания экземпляра класса и внедрения в него макетов, созданных с помощью аннотации @Mock.
    //Это позволяет легко интегрировать макеты в тестируемый класс.
    //
    //Пример использования @InjectMocks:
    //
    //@Mock
    //List mockedList;
    //
    //@InjectMocks
    //ArrayList realList;
    //В этом примере создается реальный объект ArrayList и внедряется в него макет списка. Это позволяет тестировать реальный объект с использованием макета.
    //
    //Вывод
    //Таким образом, основное различие между @Mock и @InjectMocks заключается в том, что @Mock используется для создания макета,
    //а @InjectMocks — для внедрения этих макетов в реальный объект.
    //Оба этих элемента играют важную роль в unit-тестировании и помогают создавать более надежные и изолированные тесты.
    @InjectMocks
    private AuthService authService;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenUtils jwtTokenUtils;
    private final String USERNAME = "admin";
    private final String PASSWORD = "admin";
    //UsernamePasswordAuthenticationToken Реализация org.springframework.security.core.Authentication
    //предназначена для простого представления имени пользователя и пароля.
    //Участник и учетные данные должны быть заданы с помощью объекта, предоставляющего соответствующее свойство с помощью метода Object.toString ().
    //Простейшим таким объектом является String.
    private final UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(USERNAME, PASSWORD);
    //UUID Класс, представляющий неизменяемый универсальный уникальный идентификатор (UUID). UUID представляет 128-битное значение.
    //Существуют различные варианты этих глобальных идентификаторов. Методы этого класса предназначены для манипулирования вариантом Leach-Salz,
    //хотя конструкторы допускают создание любого варианта UUID (описанного ниже).
    //Макет UUID варианта 2 (Leach-Salz) выглядит следующим образом: Наиболее значимый длинный состоит из следующих неподписанных полей:
    //0xFFFFFFFF00000000 time_low
    //0x00000000FFFF0000 time_mid
    //0x000000000000F000 версия
    //0x0000000000000FFF time_hi

    //Поле варианта содержит значение, определяющее формат UUID. Описанная выше компоновка битов действительна только для UUID со значением варианта 2,
    //что указывает на вариант Leach-Salz.
    //В поле версии содержится значение, описывающее тип этого UUID. Существует четыре основных типа UUID: временные, безопасность DCE,
    //имена и произвольно сгенерированные UUID. Эти типы имеют значение версии 1, 2, 3 и 4 соответственно.
    //Дополнительные сведения, включая алгоритмы, используемые для создания UUID, см. в RFC 4122: Универсально уникальный IDentifier (UUID)
    //Пространство имен URN, раздел 4.2 «Алгоритмы создания UUID на основе времени».

    //randomUUID() Статический завод для извлечения UUID типа 4 (псевдослучайно сгенерированный).
    //UUID генерируется с использованием криптографически сильного генератора псевдослучайных чисел.
    //Возврат:
    //Произвольно сгенерированный UUID
    private final String token = UUID.randomUUID().toString();
    private final AuthRequest authRequest = new AuthRequest(USERNAME, PASSWORD);

    @Test
    void loginUserTest() {
        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        //см. исходный файл Mockito.when (Object) --->
        //Включает методы заглушки. Используйте его, если хотите, чтобы макет возвращал определенное значение при вызове определенного метода.
        //Проще говоря: «Когда вызывается метод x, верните y».
        //Примеры:
        //when(mock.someMethod()).thenReturn(10);

        //ниже возвращаем токен наш
        given(jwtTokenUtils.generateToken(authentication)).willReturn(token);
        //и сравниваем его
        assertEquals(token, authService.loginUser(authRequest));
    }
}