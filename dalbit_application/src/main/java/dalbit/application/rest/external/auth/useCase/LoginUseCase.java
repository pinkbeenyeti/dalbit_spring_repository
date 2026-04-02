package dalbit.application.rest.external.auth.useCase;

import dalbit.application.rest.external.auth.dto.SocialLoginResult;

public interface LoginUseCase {
    SocialLoginResult socialLogin(String provider, String token);
}
