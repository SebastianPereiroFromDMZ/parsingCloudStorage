package ru.netology.services;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.netology.model.SecurityUser;
import ru.netology.repositories.UserRepository;

/**
 * @Service: Рассматривайте @Component аннотацию как швейцарский нож . Он может действовать как режущий нож, открывалка, ножницы и т. д.
 * Аналогично, ваш Компонент может действовать как репозиторий, как класс бизнес-логики или как контроллер.
 * Это @Service всего лишь одна из версий @Component, скажем, ножа.
 * Процесс Spring @Service аналогичен @Component, поскольку @Service сам интерфейс помечен @Component.
 * В чем разница?
 * Чтобы применить основное правило программирования: ваш код должен быть легко читаемым.
 * Да, вы можете использовать @Component аннотации везде, и они будут работать нормально,
 * но для лучшего понимания кода предпочтительнее использовать соответствующие специальные типы аннотаций, как @Service в нашем случае.
 * Другое преимущество — простота отладки. Как только вы узнаете об ошибке, вам не нужно будет переходить от одного класса компонента к другому,
 * проверяя время от времени, является ли этот класс сервисом, репозиторием или контроллером
 */
@Service

/**
 * UserDetailsService:
 * Основной интерфейс, который загружает пользовательские данные.
 * Он используется во всей платформе как пользовательский DAO и является стратегией, используемой DaoAuthenticationProvider.
 * Для интерфейса требуется только один метод, доступный только для чтения, что упрощает поддержку новых стратегий доступа к данным.
 * UserDetailsService используется для DaoAuthenticationProvider получения имени пользователя, пароля
 * и других атрибутов для аутентификации с использованием имени пользователя и пароля.
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Общедоступный интерфейс UserDetails расширяет Serializable
     * Предоставляет основную информацию о пользователе.
     * Реализации не используются непосредственно Spring Security в целях безопасности.
     * Они просто хранят пользовательскую информацию, которая позже инкапсулируется в Authentication объекты.
     * Это позволяет хранить информацию пользователя, не связанную с безопасностью
     * (например, адреса электронной почты, номера телефонов и т. д.), в удобном месте.
     * Конкретные реализации должны уделять особое внимание обеспечению соблюдения ненулевого контракта, подробно описанного для каждого метода.
     * См User. эталонную реализацию (которую вы, возможно, захотите расширить или использовать в своем коде)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository
                .findByUsername(username)
                .map(SecurityUser::new)
                .orElseThrow(() -> new UsernameNotFoundException("Username" + username + "not found"));

    }
}