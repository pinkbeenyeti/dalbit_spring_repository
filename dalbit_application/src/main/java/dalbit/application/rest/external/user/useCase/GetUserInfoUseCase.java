package dalbit.application.rest.external.user.useCase;

import dalbit.domain.user.User;

public interface GetUserInfoUseCase {
    User getUserInfo(Long userId);
}
