package dalbit.domain.user;

import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;

import java.util.regex.Pattern;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class UserEmail {

    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
    private final String value;

    private UserEmail(String value) {
        validateEmail(value);
        this.value = value;
    }

    private void validateEmail(String value) {
        if (value == null || !EMAIL_PATTERN.matcher(value).matches()) {
            throw new DalbitException(ErrorCode.INVALID_EMAIL_FORMAT);
        }
    }

    public static UserEmail of(String value) {
        return new UserEmail(value);
    }
}
