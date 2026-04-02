package dalbit.adapter.auth.jwt;

import dalbit.application.auth.jwt.port.GenerateTokenPort;
import dalbit.application.auth.jwt.port.VerifyTokenPort;
import dalbit.domain.user.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;

import jakarta.annotation.PostConstruct;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider implements GenerateTokenPort, VerifyTokenPort {

    private static final String AUTHORITIES_KEY = "auth";
    private final JwtProperties properties;
    private Key secretKey;

    @PostConstruct
    protected void init() {
        secretKey = Keys.hmacShaKeyFor(properties.secretKey().getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public String createAccessToken(String userExternalId, Role role) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + properties.accessTokenExpiration());

        return Jwts.builder()
            .setSubject(String.valueOf(userExternalId))
            .claim(AUTHORITIES_KEY, role.name())
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    @Override
    public String createRefreshToken(String userExternalId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + properties.refreshTokenExpiration());

        return Jwts.builder()
            .setSubject(String.valueOf(userExternalId))
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(secretKey, SignatureAlgorithm.HS256)
            .compact();
    }

    @Override
    public String getExternalIdFromToken(String token) {
        return parseClaims(token).getSubject();
    }

    @Override
    public String getRoleFromToken(String token) {
        return parseClaims(token).get(AUTHORITIES_KEY, String.class);
    }

    @Override
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
        }

        return false;
    }

    private Claims parseClaims(String token) {
        return Jwts.parserBuilder()
            .setSigningKey(secretKey)
            .build()
            .parseClaimsJws(token)
            .getBody();
    }
}
