package ru.freelib.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ru.freelib.config.JwtConfig;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class JwtTokenProvider {

    private final JwtConfig jwtConfig;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtConfig.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String createAccessToken(String login, Collection<? extends GrantedAuthority> authorities) {
        return buildToken(login, authorities, "ACCESS", jwtConfig.getAccessTtl());
    }

    public String createRefreshToken(String login, Collection<? extends GrantedAuthority> authorities) {
        return buildToken(login, authorities, "REFRESH", jwtConfig.getRefreshTtl());
    }

    private String buildToken(String login, Collection<? extends GrantedAuthority> authorities, String type, long ttl) {
        String roles = authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));
        Date now = new Date();
        return Jwts.builder()
                .subject(login)
                .claim("roles", roles)
                .claim("type", type)
                .issuedAt(now)
                .expiration(new Date(now.getTime() + ttl))
                .signWith(getSigningKey(), Jwts.SIG.HS256)
                .compact();
    }

    public boolean validateToken(String token, String expectedType) {
        try {
            Claims claims = Jwts.parser().verifyWith(getSigningKey()).build()
                    .parseSignedClaims(token).getPayload();
            return expectedType.equals(claims.get("type", String.class));
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    public String getLogin(String token) {
        return Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().getSubject();
    }

    public List<String> getRoles(String token) {
        String roles = Jwts.parser().verifyWith(getSigningKey()).build()
                .parseSignedClaims(token).getPayload().get("roles", String.class);
        return roles != null ? List.of(roles.split(",")) : List.of();
    }
}