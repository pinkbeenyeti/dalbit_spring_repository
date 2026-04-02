package dalbit.adapter.rest.external.user.dto.response;

import dalbit.domain.user.User;

public record UserResponse(
    String externalId,
    String name,
    String email
) {
    public static UserResponse from(User user) {
        return new UserResponse(
            user.getExternalId(),
            user.getName().getValue(),
            user.getEmail().getValue()
        );
    }
}
