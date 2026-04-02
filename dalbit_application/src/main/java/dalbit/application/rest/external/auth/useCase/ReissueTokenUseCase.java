package dalbit.application.rest.external.auth.useCase;

import dalbit.application.rest.external.auth.dto.TokenResult;

public interface ReissueTokenUseCase {
    TokenResult reissue(String refreshToken);
}
