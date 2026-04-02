package dalbit.adapter.rest.common.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    int code,
    String message,
    T data
) {

    public static ResponseEntity<ApiResponse<Void>> success() {
        return ResponseEntity.ok(
            new ApiResponse<>(200, "요청이 성공적으로 처리되었습니다.", null)
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> success(T data) {
        return ResponseEntity.ok(
            new ApiResponse<>(200, "요청이 성공적으로 처리되었습니다.", data)
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> created(T data) {
        return ResponseEntity.status(HttpStatus.CREATED).body(
            new ApiResponse<>(201, "요청이 성공적으로 처리되었습니다.", data)
        );
    }

    public static <T> ResponseEntity<ApiResponse<T>> fail(HttpStatus status, int code, String message) {
        return ResponseEntity.status(status).body(
            new ApiResponse<>(code, message, null)
        );
    }
}
