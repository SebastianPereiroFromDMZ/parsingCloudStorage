package ru.netology.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.netology.services.UserDetailsServiceImpl;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenUtils jwtTokenUtils;
    private final UserDetailsServiceImpl userDetailsService;

    public JwtTokenFilter(JwtTokenUtils jwtTokenUtils, UserDetailsServiceImpl userDetailsService) {
        this.jwtTokenUtils = jwtTokenUtils;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = parseToken(request);
            if (token != null && jwtTokenUtils.validateToken(token)) {
                String username = jwtTokenUtils.getUsernameFromToken(token);
                var userDetails = userDetailsService.loadUserByUsername(username);
                var authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        } catch (Exception ex) {
            ex.getMessage();
        }
        filterChain.doFilter(request, response);
    }

    private String parseToken(HttpServletRequest request) {
        String authToken = request.getHeader("auth-token");
        if (StringUtils.hasText(authToken) && authToken.startsWith("Bearer ")) {
            return authToken.substring(7);
        }
        return null;
    }
}