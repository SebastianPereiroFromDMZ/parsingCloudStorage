package ru.netology.configs;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import ru.netology.security.JwtEntryPoint;
import ru.netology.security.JwtTokenFilter;
import ru.netology.services.UserDetailsServiceImpl;

import java.util.List;

/**
 * @Configuration – это аннотация на уровне класса, указывающая на то, что объект является источником определений бина.
 * Классы, аннотированные @Configuration, объявляют бины через методы, аннотированные @Bean.
 * Вызовы методов @Bean для классов @Configuration также могут быть использованы для определения межбиновых зависимостей.
 */
@Configuration
/**
 * @EnableWebSecurity включает Spring Security в нашем проэкте
 */
@EnableWebSecurity
public class SecurityConfig {
    private final UserDetailsServiceImpl userDetailsService;
    private final JwtEntryPoint jwtEntryPoint;
    public final JwtTokenFilter jwtTokenFilter;

    public SecurityConfig(UserDetailsServiceImpl userDetailsService, JwtEntryPoint jwtEntryPoint, JwtTokenFilter jwtTokenFilter) {
        this.userDetailsService = userDetailsService;
        this.jwtEntryPoint = jwtEntryPoint;
        this.jwtTokenFilter = jwtTokenFilter;
    }

    /**
     * Общедоступный интерфейс PasswordEncoder
     * Сервисный интерфейс для шифрования паролей. Предпочтительной реализацией является BCryptPasswordEncoder.
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * DaoAuthenticationProvider— это AuthenticationProvider реализация, которая использует UserDetailsService и PasswordEncoder для
     * аутентификации имени пользователя и пароля.
     * <p>
     * Публичный класс DaoAuthenticationProvider  расширяет AbstractUserDetailsAuthenticationProvider
     * Реализация AuthenticationProvider, которая извлекает данные пользователя из файла UserDetailsService
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    /**
     * Общедоступный интерфейс AuthenticationManager Обрабатывает Authentication запрос.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }


    /**
     * Общедоступный интерфейс SecurityFilterChain
     * Определяет цепочку фильтров, которую можно сопоставить с файлом HttpServletRequest. чтобы решить, применимо ли это к этому запросу.
     * Используется для настройки FilterChainProxy.
     * Подробнее в статье от JavaRush ссылка в файле links
     * <p>
     * <p>
     * Ппубличный конечный класс HttpSecurity
     * расширяет AbstractConfiguredSecurityBuilder < DefaultSecurityFilterChain , HttpSecurity >
     * реализует SecurityBuilder < DefaultSecurityFilterChain >, HttpSecurityBuilder < HttpSecurity >
     * A HttpSecurity аналогичен элементу XML <http> Spring Security в конфигурации пространства имен.
     * Он позволяет настраивать веб-безопасность для определенных HTTP-запросов. По умолчанию оно будет применяться ко всем запросам,
     * но его можно ограничить с помощью #requestMatcher(RequestMatcher)других подобных методов
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        //cors совместное использование ресурсов между разными источниками
        //csrf подделка межсайтовых запросов
        //отключаем эти два фильтра
        http.cors().and().csrf().disable();
        //addFilterBefore() позволяет добавить Filter перед одним из известных Filter классов.
        // Известные Filter экземпляры либо Filter указаны в списке HttpSecurityBuilder.addFilter(Filter),
        // либо Filter уже добавлены с помощью HttpSecurityBuilder.addFilterAfter(Filter, Class)или HttpSecurityBuilder.addFilterBefore(Filter, Class).
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);
        //authenticationProvider() позволяет добавить дополнительный AuthenticationProvider для использования
        http.authenticationProvider(authenticationProvider());
        http
                .authorizeRequests().mvcMatchers("/login").permitAll()
                .anyRequest().authenticated()

                .and()
                .logout()
                .deleteCookies("JSESSIONID")
                .clearAuthentication(true)

                .and()
                .exceptionHandling()
                .authenticationEntryPoint(jwtEntryPoint)

                .and()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:8080/"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowCredentials(true);
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}