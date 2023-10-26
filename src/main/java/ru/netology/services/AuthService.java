package ru.netology.services;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ru.netology.dto.AuthRequest;
import ru.netology.security.JwtTokenUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {
    //AuthenticationManager Обрабатывает запрос на аутентификацию.
    private final AuthenticationManager authenticationManager;
    private final JwtTokenUtils jwtTokenUtils;
    private final Map<String, String> tokenStore = new HashMap<>();

    public AuthService(AuthenticationManager authenticationManager, JwtTokenUtils jwtTokenUtils) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenUtils = jwtTokenUtils;
    }

    public String loginUser(AuthRequest authRequest) {
        try {
            //Authentication Представляет токен для запроса аутентификации или для прошедшего проверку участника после обработки запроса методом
            //AuthenticationManager.authenticate(Аутентификация).
            //Как только запрос был аутентифицирован, аутентификация обычно сохраняется в локальном потоке SecurityContext,
            //управляемом владельцем SecurityContextHolder с помощью используемого механизма аутентификации.
            //Явная аутентификация может быть достигнута без использования одного из механизмов аутентификации Spring Security
            //путем создания экземпляра аутентификации и использования кода:
            //SecurityContext context = SecurityContextHolder.createEmptyContext();
            //context.setAuthentication(anAuthentication);
            //SecurityContextHolder.setContext(context);
            //
            //Обратите внимание, что если для аутентификации не установлено значение true для свойства authenticated,
            //оно все равно будет аутентифицировано любым перехватчиком безопасности (для методов или веб-вызовов), который с ним сталкивается.
            //В большинстве случаев фреймворк прозрачно заботится об управлении безопасностью


            //authenticationManager.authenticate Пытается аутентифицировать переданный объект аутентификации, возвращая в случае успеха полностью заполненный объект аутентификации
            //(включая предоставленные полномочия).
            //Менеджер аутентификации должен соблюдать следующий контракт, касающийся исключений:
            //Исключение DisabledException должно быть вызвано, если учетная запись отключена, и AuthenticationManager может проверить это состояние.
            //Исключение LockedException должно быть вызвано, если учетная запись заблокирована,
            //и AuthenticationManager может проверить наличие блокировки учетной записи.
            //При представлении неверных учетных данных должно быть выдано исключение BadCredentialsException.
            //Хотя вышеуказанные исключения являются необязательными, AuthenticationManager всегда должен проверять учетные данные.
            //Исключения должны быть проверены и, если применимо, выброшены в указанном выше порядке (т.е. если учетная запись отключена или заблокирована,
            //запрос на аутентификацию немедленно отклоняется и процесс проверки учетных данных не выполняется).
            //Это предотвращает проверку учетных данных на отключенных или заблокированных учетных записях.
            //Параметры:
            //аутентификация – объект запроса аутентификации
            //Возвращается:
            //полностью аутентифицированный объект, включая учетные данные
            //Бросает:
            //AuthenticationException – при сбое аутентификации
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(authRequest.getLogin(),authRequest.getPassword()));
            //SecurityContextHolder Связывает данный SecurityContext с текущим потоком выполнения.
            //Этот класс предоставляет ряд статических методов, которые делегируются экземпляру SecurityContextHolderStrategy.
            //Цель класса - предоставить удобный способ указать стратегию, которая должна использоваться для данной JVM.
            //Это настройка для всей JVM, поскольку все в этом классе статично для облегчения использования при вызове кода.
            //Чтобы указать, какую стратегию следует использовать, вы должны указать настройку режима.
            //Параметр mode - это один из трех допустимых параметров MODE_, определенных как статические конечные поля,
            //или полное имя класса для конкретной реализации SecurityContextHolderStrategy, которая предоставляет открытый конструктор без аргументов.
            //Существует два способа указать строку желаемого режима стратегии. Первый - указать его с помощью системного свойства,
            //указанного в SYSTEM_PROPERTY. Второй - вызвать setStrategyName(String) перед использованием класса.
            //Если не используется ни один из подходов, класс по умолчанию будет использовать MODE_THREADLOCAL, который обратно совместим,
            //имеет меньше несовместимостей с JVM и подходит для серверов (тогда как MODE_GLOBAL определенно не подходит для использования на сервере)

            //SecurityContext Интерфейс, определяющий минимальную информацию о безопасности, связанную с текущим потоком выполнения.
            //Контекст безопасности хранится в SecurityContextHolder.

            //getContext() Получите текущий SecurityContext.
            //Возвращается:
            //контекст безопасности (никогда не null)

            //setAuthentication Изменяет текущего аутентифицированного участника или удаляет аутентификационную информацию.
            //Параметры:
            //аутентификация – новый токен аутентификации или значение null, если не требуется сохранять дополнительную аутентификационную информацию
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtTokenUtils.generateToken(authentication);
            tokenStore.put(token, authRequest.getLogin());
            return token;

        } catch (AuthenticationException ex) {
            throw new BadCredentialsException("Bad credentials");
        }
    }

    public void logoutUser(String authToken) {
        tokenStore.remove(authToken);
    }
}