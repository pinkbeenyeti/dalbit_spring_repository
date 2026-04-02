package dalbit.domain.device.moon;

import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class Volume {

    private static final int MIN_VALUE = 0;
    private static final int MAX_VALUE = 100;
    private final int value;

    private Volume(int value) {
        validate(value);
        this.value = value;
    }

    private void validate(int value) {
        if (value < MIN_VALUE || value > MAX_VALUE) {
            throw new DalbitException(ErrorCode.INVALID_VOLUME_VALUE);
        }
    }

    public static Volume of(int value) {
        return new Volume(value);
    }
}
