package dalbit.adapter.auth.security.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(
        HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {

        log.warn("[인가 실패] 권한이 없는 API 접근 시도. URI: {}", request.getRequestURI());

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");
        response.setStatus(HttpStatus.FORBIDDEN.value());

        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("code", -10011);
        errorResponse.put("message", "해당 요청에 대한 접근 권한이 없습니다.");

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}
