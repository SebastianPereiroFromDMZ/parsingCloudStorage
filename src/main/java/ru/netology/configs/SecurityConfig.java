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
                //authorizeRequests() Позволяет ограничить доступ на основе HttpServletRequest используемых
                //RequestMatcher реализаций (т. е. с помощью шаблонов URL-адресов). Устарело.
                //Будет удален в  7.0. Вместо этого используйте authorizeHttpRequests()
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

    /**
     * CorsConfigurationSource
     * Интерфейс, реализуемый классами (обычно обработчиками HTTP-запросов), который предоставляет экземпляр CorsConfiguration
     * на основе предоставленного запроса.
     */

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        //CorsConfiguration контейнер для конфигурации CORS, а также методы проверки фактического источника, методы HTTP и заголовки данного запроса.
        //По умолчанию вновь созданная CorsConfiguration не разрешает запросы между источниками и должна быть настроена явно,
        // чтобы указать, что следует разрешить. Используйте applyPermitDefaultValues(), чтобы перевернуть модель инициализации,
        // чтобы начать с открытых значений по умолчанию, которые разрешают все запросы между источниками для запросов GET, HEAD и POST.
        final CorsConfiguration configuration = new CorsConfiguration();
        //setAllowedOrigins() список источников, для которых разрешены запросы между источниками. Значения могут быть конкретным доменом,
        //например: «https://domain1.com» или CORS определил специальное значение «*» для всех источников.
        //Для совпадающих предполетных и фактических запросов заголовок ответа Access-Control-Allow-Origin устанавливается либо на соответствующее значение домена,
        // либо на «*». Однако имейте в виду, что спецификация CORS не допускает «*», когда для параметраallowCredentials установлено значение true,
        // и начиная с версии 5.3 эта комбинация отклоняется в пользу использования вместо нее разрешенногоOriginPatterns.
        //По умолчанию этот параметр не установлен, что означает, что никакие источники не разрешены.
        // Однако экземпляр этого класса часто инициализируется дальше, например. для @CrossOrigin через applyPermitDefaultValues().
        configuration.setAllowedOrigins(List.of("http://localhost:8080/"));
        //setAllowedHeaders() установите список заголовков, которые в предварительном запросе могут быть указаны как разрешенные для использования
        //во время фактического запроса.
        //Специальное значение «*» позволяет фактическим запросам отправлять любой заголовок.
        //Имя заголовка не обязательно указывать, если оно относится к одному из следующих типов:
        //Cache-Control, Content-Language, Expires, Last-Modified или Pragma.
        //По умолчанию это не установлено
        configuration.setAllowedHeaders(List.of("*"));
        //setAllowedMethods() установите разрешенные методы HTTP, например. «ПОЛУЧИТЬ», «ОТПРАВИТЬ», «ПОСТАВИТЬ» и т. д.
        //Специальное значение «*» разрешает использование всех методов.
        //Если не установлено, разрешены только «GET» и «HEAD».
        //По умолчанию это не установлено.
        //Примечание. При проверке CORS используются значения из заголовков «Forwarded» (RFC 7239),
        //«X-Forwarded-Host», «X-Forwarded-Port» и «X-Forwarded-Proto», если они присутствуют,
        //чтобы отразить клиент. - исходный адрес. Рассмотрите возможность использования ForwardedHeaderFilter, чтобы централизованно выбирать,
        //следует ли извлекать и использовать такие заголовки или отбрасывать их. Дополнительную информацию об этом фильтре см.
        //в справочнике по Spring Framework.
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        //etAllowCredentials() поддерживаются ли учетные данные пользователя.
        //По умолчанию это значение не установлено (т. е. учетные данные пользователя не поддерживаются).
        configuration.setAllowCredentials(true);
        //UrlBasedCorsConfigurationSource: CorsConfigurationSource, который использует шаблоны URL-адресов для выбора CorsConfiguration для запроса.
        //Сопоставление с образцом можно выполнить с помощью PathMatcher или предварительно проанализированных PathPatterns.
        //Синтаксис во многом такой же, последний более адаптирован для использования в Интернете и более эффективен.
        //Выбор зависит от наличия разрешенного String LookupPath или проанализированного RequestPath с резервным вариантом PathMatcher,
        //но резервный вариант можно отключить. Дополнительные сведения см. в разделе setAllowInitLookupPath(boolean).
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        //Вариант setCorsConfigurations(Map) для одновременной регистрации одного сопоставления.
        //Параметры:
        //шаблон – конфигурация шаблона сопоставления – конфигурация CORS, используемая для шаблона
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}