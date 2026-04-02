package dalbit.application.auth.jwt.port;


import dalbit.domain.user.Role;

public interface GenerateTokenPort {
    String createAccessToken(String userExternalId, Role role);
    String createRefreshToken(String userExternalId);
}
