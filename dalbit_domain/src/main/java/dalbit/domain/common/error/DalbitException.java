package dalbit.domain.common.error;

import lombok.Getter;

@Getter
public class DalbitException extends RuntimeException {

    private final ErrorCode errorCode;

    public DalbitException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }
}
