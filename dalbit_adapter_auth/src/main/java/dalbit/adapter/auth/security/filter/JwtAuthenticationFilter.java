package dalbit.adapter.auth.security.filter;

import dalbit.application.auth.jwt.port.VerifyTokenPort;
import dalbit.application.persistence.jpa.user.port.LoadUserPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter  {

    private final VerifyTokenPort verifyTokenPort;
    private final LoadUserPort loadUserPort;

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try {
            String token = extractToken(request);

            if (token != null && verifyTokenPort.validateToken(token)) {
                String userExternalId = verifyTokenPort.getExternalIdFromToken(token);
                String role = verifyTokenPort.getRoleFromToken(token);

                Long userId = loadUserPort.loadUserIdByExternalId(userExternalId)
                    .orElseThrow(() -> new DalbitException(ErrorCode.NOT_EXIST_USER));

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userId,
                    token,
                    Collections.singletonList(new SimpleGrantedAuthority(role))
                );

                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        } catch (Exception e) {
            log.error("[Sprint Security] jwt 토큰 인증 중 에러 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            return token.substring(7);
        }

        return null;
    }
}
