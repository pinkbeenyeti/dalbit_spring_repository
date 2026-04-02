package dalbit.application.auth.jwt.port;

public interface VerifyTokenPort {
    String getExternalIdFromToken(String token);
    String getRoleFromToken(String token);
    boolean validateToken(String token);
}
