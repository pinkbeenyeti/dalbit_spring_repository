package dalbit.adapter.rest.external.user.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateUserNameRequest(
    @NotBlank(message = "변경할 이름은 필수 입력값입니다.")
    @Size(max = 10, message = "이름은 10자를 초과할 수 없습니다.")
    String name
) {}
