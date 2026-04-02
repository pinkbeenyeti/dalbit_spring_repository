package dalbit.adapter.rest.common.exception;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DalbitException.class)
    public ResponseEntity<ApiResponse<Void>> handleDalbitException(DalbitException e) {
        ErrorCode errorCode = e.getErrorCode();

        log.warn("DALBIT 예외 발생: code={}, message={}", errorCode.getCode(), errorCode.getMessage());

        return ApiResponse.fail(
            HttpStatus.resolve(errorCode.getStatus()),
            errorCode.getCode(),
            errorCode.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationException(MethodArgumentNotValidException e) {

        String errorMessage = e.getBindingResult().getFieldErrors().stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.joining(", "));

        log.warn("[ValidationException] 잘못된 입력값: {}", errorMessage);

        return ApiResponse.fail(
            HttpStatus.BAD_REQUEST,
            -20010,
            errorMessage
        );
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleNoResourceFoundException(org.springframework.web.servlet.resource.NoResourceFoundException e) {
        log.warn("[404 Not Found] 존재하지 않는 경로 요청: {}", e.getResourcePath());

        return ApiResponse.fail(
            org.springframework.http.HttpStatus.NOT_FOUND,
            -40400,
            "요청하신 경로를 찾을 수 없습니다."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleUnhandledException(Exception e) {

        log.error("[UnhandledException] 예상치 못한 서버 에러가 발생했습니다.", e);

        return ApiResponse.fail(
            HttpStatus.INTERNAL_SERVER_ERROR,
            -50000,
            "서버 내부에서 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."
        );
    }
}
