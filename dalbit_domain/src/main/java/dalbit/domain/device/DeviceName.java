package dalbit.domain.device;

import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class DeviceName {
    private final String value;

    private static final int MAX_LENGTH = 20;
    private static final String NAME_PATTERN_REGEX = "^[a-zA-Z0-9가-힣\\-_ ]+$";
    private static final Pattern NAME_PATTERN = Pattern.compile(NAME_PATTERN_REGEX);

    private DeviceName(String value) {
        validate(value);
        this.value = value;
    }

    private void validate(String value) {
        if (value == null || value.isBlank()) {
            throw new DalbitException(ErrorCode.INVALID_DEVICE_NAME);
        }

        if (value.length() > MAX_LENGTH) {
            throw new DalbitException(ErrorCode.INVALID_DEVICE_NAME);
        }

        if (!NAME_PATTERN.matcher(value).matches()) {
            throw new DalbitException(ErrorCode.INVALID_DEVICE_NAME);
        }
    }

    public static DeviceName of(String value) {
        return new DeviceName(value);
    }
}
