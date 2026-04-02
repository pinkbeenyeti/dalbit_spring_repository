package dalbit.adapter.auth.jwt;


import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("jwt")
public record JwtProperties(
    String secretKey,
    Long accessTokenExpiration,
    Long refreshTokenExpiration
) {

}
