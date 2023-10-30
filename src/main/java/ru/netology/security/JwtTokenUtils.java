package ru.netology.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import ru.netology.model.SecurityUser;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Component
public class JwtTokenUtils {
    @Value("${jwt.signingKey}")
    private String signingKey;

    public String generateToken(Authentication authentication) {
        //getPrincipal() Личность принципала, проходящего проверку подлинности. В случае запроса на аутентификацию с использованием имени пользователя и пароля,
        //это будет имя пользователя. Ожидается, что вызывающие абоненты заполнят имя участника для запроса аутентификации.
        //Реализация AuthenticationManager часто возвращает аутентификацию, содержащую более подробную информацию в качестве
        //основной для использования приложением. Многие поставщики аутентификации создадут объект UserDetails в качестве основного.
        //Возвращается:
        //участник, проходящий аутентификацию, или прошедший аутентификацию участник после аутентификации.
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        //Instant Этот класс моделирует одну мгновенную точку на временной линии. Это может быть использовано для записи временных меток событий в приложении

        //now() Получает текущий момент времени из системных часов.
        //Это приведет к запросу системных часов UTC для получения текущего момента времени.
        //Использование этого метода лишит возможности использовать альтернативный источник времени для тестирования,
        //поскольку часы фактически жестко запрограммированы.
        //Возвращается:
        //текущий момент времени с использованием системных часов, а не null
        Instant now = Instant.now();
        //SecretKey Секретный (симметричный) ключ. Цель этого интерфейса - сгруппировать (и обеспечить безопасность типов для) все интерфейсы с секретным ключом.
        //Реализации поставщика этого интерфейса должны перезаписывать методы equals и hashCode, унаследованные от Object,
        //чтобы секретные ключи сравнивались на основе их базового ключевого материала, а не на основе ссылки.
        //Реализации должны переопределять методы destroy и isDestroyed по умолчанию из javax.security.auth.Уничтожаемый интерфейс,
        //позволяющий уничтожать, очищать конфиденциальную ключевую информацию или, в случае, когда такая информация является неизменяемой,
        //не использовать ссылки. Наконец, поскольку SecretKey сериализуем, реализации также должны переопределять
        //java.io.ObjectOutputStream.writeObject(объект), чтобы предотвратить сериализацию ключей, которые были уничтожены.
        //Ключи, реализующие этот интерфейс, возвращают строку RAW в качестве своего формата кодировки (см. getFormat)
        //и возвращают необработанные байты ключа в результате вызова метода getEncoded.
        //(Методы getFormat и getEncoded находятся в java.security.Key parent interface.)

        //Keys Служебный класс для безопасной генерации секретных ключей и пар клавиш.

        //hmacShaKeyFor Создает новый экземпляр SecretKey для использования с алгоритмами HMAC-SHA на основе указанного массива байтов ключа.
        //Параметры: байты – массив байтов ключа
        //Возвращается: новый экземпляр SecretKey для использования с алгоритмами HMAC-SHA, основанными на указанном массиве байтов ключа.
        //Бросает:
        //Исключение WeakKeyException – если длина массива байтов ключа меньше 256 бит (32 байта), как предписано спецификацией JWT JWA (RFC 7518, раздел 3.2)
        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        //Jwts Фабричный класс, полезный для создания экземпляров интерфейсов JWT.
        //Использование этого фабричного класса может быть хорошей альтернативой тесной привязке вашего кода к классам реализации.

        //builder() Возвращает новый экземпляр JwtBuilder, который можно настроить и затем использовать для создания компактных сериализованных строк JWT.
        //Возвращается:
        //новый экземпляр JwtBuilder, который можно настроить и затем использовать для создания компактных сериализованных строк JWT
        return Jwts.builder()
                //setSubject Устанавливает значение параметра JWT Claims sub (обьект). Значение null приведет к удалению свойства из утверждений.
                //Это удобный метод. Сначала он убедится, что экземпляр Claims существует в качестве тела JWT,
                //а затем установит в поле Claims subject указанное значение. Это позволяет вам писать код, подобный этому:
                .setSubject(securityUser.getUsername())
                //setIssuedAt Устанавливает значение JWT Claims iat (выдано по адресу). Значение null приведет к удалению свойства из утверждений.
                //Значение - это временная метка, когда был создан JWT.
                //Это удобный метод. Сначала он убедится, что экземпляр Claims существует в качестве тела JWT,
                //а затем присвоит полю Claims issuedAt указанное значение.
                .setIssuedAt(Date.from(now))
                //setExpiration Устанавливает значение JWT Claims exp (истечение срока действия). Значение null приведет к удалению свойства из утверждений.
                //JWT, полученный после этой временной метки, использовать не следует.
                //Это удобный метод. Сначала он убедится, что экземпляр Claims существует в качестве тела JWT,
                //а затем установит в поле истечения срока действия Claims указанное значение.
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                //signWith Подписывает созданный JWT указанным ключом, используя рекомендуемый алгоритм подписи ключа, создавая JWS.
                //Если рекомендуемый алгоритм подписи недостаточен для ваших нужд,
                //рассмотрите возможность использования вместо него signWith(Key, signatureAlgorithm).
                //Если вы хотите вызвать этот метод с массивом байтов, который, как вы уверены, может использоваться для алгоритмов HMAC-SHA,
                //рассмотрите возможность использования Keys.hmacShaKeyFor(bytes) для преобразования массива байтов в допустимый ключ.
                .signWith(key)
                //compact Фактически создает JWT и сериализует его в компактную строку, безопасную для URL-адресов,
                //в соответствии с правилами компактной сериализации JWT.
                .compact();
    }

    public Boolean validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        try {
            //Jwts Заводской класс полезен для создания экземпляров интерфейсов JWT.
            //Использование этого заводского класса может быть хорошей альтернативой тесной увязке кода с классами

            //parserBuilder() Возвращает новый экземпляр JwtParserBuilder, который можно настроить для создания неизменяемого/безопасного для потоков JwtParser.
            //Возврат:
            //новый экземпляр JwtParser, который можно настроить, создает неизменяемый/защищенный
            Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return true;
        } catch (MalformedJwtException ex) {
            ex.getMessage();
        }
        return false;
    }

    public String getUsernameFromToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody().getSubject();
    }
}