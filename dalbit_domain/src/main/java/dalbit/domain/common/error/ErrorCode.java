package dalbit.domain.common.error;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorCode {
    FAIL_SEND_COMMAND(400, -1, "명령 전송에 실패하였습니다"),

    NOT_EXIST_REFRESH_TOKEN(403, -10000, "존재하지 않는 리프레시 토큰입니다."),
    NOT_EXIST_USER(400, -10001, "존재하지 않는 유저입니다"),
    NOT_EXIST_USER_DEVICE(400, -10002, "존재하지 않은 유저의 디바이스입니다."),
    NOT_EXIST_DEVICE(400, -10003, "존재하지 않는 디바이스입니다"),
    NOT_EXIST_VOICE(400, -10004, "존재하지 않는 음성입니다"),
    NOT_EXIST_AUDIO_BOOK(403, -10005, "존재하지 않는 오디오북입니다"),
    NOT_EXIST_FAIRYTALE(400, -10006, "존재하지 않는 동화입니다."),
    NOT_DEVICE_OWNER(403, -10007, "소유자가 아닌 디바이스입니다."),

    INVALID_PROVIDER(400, -20000, "유효하지 않은 OAuth2 Provider 입니다."),
    INVALID_PROVIDER_TOKEN(401, -20001, "유효하지 않은 OAuth2 Provider의 토큰입니다."),
    INVALID_REFRESH_TOKEN(401, -20002, "유효하지 않은 Refresh 토큰입니다."),
    INVALID_EMAIL_FORMAT(400, -20003, "이메일 형식이 올바르지 않습니다"),
    INVALID_USER_NAME(400, -20004, "유저 이름 형식이 올바르지 않습니다"),
    INVALID_DEVICE_NAME(400, -20005, "디바이스 이름 형식이 올바르지 않습니다"),
    INVALID_VOICE_NAME(400, -20006, "음성 이름 형식이 올바르지 않습니다"),
    INVALID_VOICE_STATUS(400, -20007, "목소리가 이미 학습 중이거나, 접근할 수 없는 목소리입니다."),
    INVALID_BRIGHTNESS_VALUE(400, -20008, "밝기 값이 올바르지 않습니다"),
    INVALID_VOLUME_VALUE(400, -20009, "볼륨 값이 올바르지 않습니다"),

    INCOMPLETE_VOICE_UPLOAD(400, -30000,"음성 파일 업로드가 완료되지 않았습니다."),

    ALREADY_EXIST_DEVICE(409, -40000, "이미 존재하는 디바이스입니다"),
    ALREADY_OWNED_DEVICE(409, -40001, "이미 소유자가 등록된 디바이스입니다"),
    ALREADY_EXIST_VOICE_NAME(409, -40002, "이미 존재하는 목소리 이름입니다."),

    INTERNAL_SERVER_ERROR(500, -50000, "서버 내부에서 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    EXTERNAL_OAUTH2_SERVER_ERROR(503, -50001, "OAuth2 외부 서버에서 오류가 발생했습니다. 잠시 후 다시 시도해주세요."),
    EXTERNAL_STORAGE_SERVER_ERROR(503, -50002, "Storage 외부 서버에서 오류가 발생했습니다. 잠시 후 시도해주세요.");

    private final int status;
    private final int code;
    private final String message;
}
