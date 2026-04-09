package dalbit.adapter.auth.security.config;

import dalbit.adapter.auth.security.exception.JwtAccessDeniedHandler;
import dalbit.adapter.auth.security.exception.JwtAuthenticationEntryPoint;
import dalbit.adapter.auth.security.filter.JwtAuthenticationFilter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // CORS 설정
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))

            // CSRF 비활성화 (Stateless 서버)
            .csrf(AbstractHttpConfigurer::disable)

            // 세션 사용 비활성화 (Stateless 서버)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // Form Login 및 HTTP Basic 인증 비활성화
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer:: disable)

            // 요청 경로별 인가 설정 (Oath 로그인/회원가입 허가, 그 외 요청 인증 필요)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/auth/**").permitAll()
                .requestMatchers("/internal/queue/auth/**").access((authentication, context) -> {
                    String clientIp = context.getRequest().getRemoteAddr();

                    boolean isLocal = "127.0.0.1".equals(clientIp) || "0:0:0:0:0:0:0:1".equals(clientIp);
                    boolean isQueue = "10.0.0.204".equals(clientIp);

                    return new AuthorizationDecision(isLocal || isQueue);
                })
                .requestMatchers("/actuator/**").access((authentication, context) -> {
                    String clientIp = context.getRequest().getRemoteAddr();

                    boolean isLocal = "127.0.0.1".equals(clientIp) || "0:0:0:0:0:0:0:1".equals(clientIp);
                    boolean isMonitor = "10.0.0.247".equals(clientIp);

                    return new AuthorizationDecision(isLocal || isMonitor);
                })
                .anyRequest().authenticated())

            // 필터 예외 처리, JWT 토큰 검증 시 발생하는 토큰 만료나, 잘못된 토큰 에러를 규격에 맞게 처리
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler)
            )

            // JWT 인증 필터 적용
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        configuration.setAllowedOrigins(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With", "Accept", "Origin"));
        configuration.setExposedHeaders(List.of("Authorization"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }

}
