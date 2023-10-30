package ru.netology.security;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


/**
 * AuthenticationEntryPoint Используется ExceptionTranslationFilter для запуска схемы аутентификации.
 */
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    /**
     * commence Запускает схему аутентификации.
     * ExceptionTranslationFilter заполнит атрибут HttpSession с именем AbstractAuthenticationProcessingFilter.SPRING_SECURITY_SAVED_REQUEST_KEY
     * запрошенным целевым URL-адресом перед вызовом этого метода.
     * Реализации должны изменить заголовки ServletResponse по мере необходимости, чтобы начать процесс аутентификации.
     * Параметры:
     * запрос – который привел к ответу AuthenticationException – чтобы пользовательский агент мог начать аутентификацию authException – который вызвал вызов
     */

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }
}