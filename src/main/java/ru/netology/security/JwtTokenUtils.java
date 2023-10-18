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
        SecurityUser securityUser = (SecurityUser) authentication.getPrincipal();
        Instant now = Instant.now();
        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        return Jwts.builder()
                .setSubject(securityUser.getUsername())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(now.plus(1, ChronoUnit.HOURS)))
                .signWith(key)
                .compact();
    }

    public Boolean validateToken(String token) {
        SecretKey key = Keys.hmacShaKeyFor(signingKey.getBytes(StandardCharsets.UTF_8));
        try {
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