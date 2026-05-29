package ru.freelib.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.freelib.config.JwtConfig;

import java.io.IOException;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtProvider;
    private final JwtConfig jwtConfig;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String access = getCookieValue(request, "ACCESS_TOKEN");
        String refresh = getCookieValue(request, "REFRESH_TOKEN");

        if (access != null && jwtProvider.validateToken(access, "ACCESS")) {
            setAuthentication(access, response);
        }
        else if (refresh != null && jwtProvider.validateToken(refresh, "REFRESH")) {
            String login = jwtProvider.getLogin(refresh);
            String newAccess = jwtProvider.createAccessToken(login, userDetailsService.loadUserByUsername(login).getAuthorities());
            setCookie(response, "ACCESS_TOKEN", newAccess, jwtConfig.getAccessTtl());
            setAuthentication(newAccess, response);
        }
        else {
            SecurityContextHolder.clearContext();
            if (access != null || refresh != null) clearCookies(response);
        }

        chain.doFilter(request, response);
    }

    private void setAuthentication(String token, HttpServletResponse response) {
        String login = jwtProvider.getLogin(token);
        UserDetails userDetails = userDetailsService.loadUserByUsername(login);

        var auth = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    private String getCookieValue(HttpServletRequest req, String name) {
        if (req.getCookies() == null) return null;
        return Arrays.stream(req.getCookies())
                .filter(c -> name.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst().orElse(null);
    }

    private void setCookie(HttpServletResponse resp, String name, String value, long ttlMs) {
        resp.addHeader("Set-Cookie", String.format(
                "%s=%s; Path=/; Max-Age=%d; HttpOnly; SameSite=Strict%s",
                name, value, ttlMs / 1000, "true".equals(System.getenv("HTTPS")) ? "; Secure" : ""
        ));
    }

    private void clearCookies(HttpServletResponse resp) {
        setCookie(resp, "ACCESS_TOKEN", "", 0);
        setCookie(resp, "REFRESH_TOKEN", "", 0);
    }
}