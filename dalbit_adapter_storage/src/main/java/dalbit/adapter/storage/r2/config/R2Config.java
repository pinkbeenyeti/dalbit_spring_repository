package dalbit.adapter.storage.r2.config;

import dalbit.adapter.storage.r2.out.R2Adapter;
import dalbit.adapter.storage.r2.property.R2Properties;
import java.net.URI;
import java.time.Duration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.http.urlconnection.UrlConnectionHttpClient;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConditionalOnProperty(name = "storage.provider", havingValue = "r2")
public class R2Config {

    @Bean(destroyMethod = "close")
    public S3Client r2Client(R2Properties r2Properties) {
        return S3Client.builder()
            .endpointOverride(URI.create(r2Properties.endpoint()))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(r2Properties.accessKey(), r2Properties.secretKey())))
            .region(Region.of("auto"))
            .httpClientBuilder(UrlConnectionHttpClient.builder()
                .connectionTimeout(Duration.ofSeconds(3))
                .socketTimeout(Duration.ofSeconds(5)))
            .build();
    }

    @Bean(destroyMethod = "close")
    public S3Presigner r2Presigner(R2Properties r2Properties) {
        return S3Presigner.builder()
            .endpointOverride(URI.create(r2Properties.endpoint()))
            .credentialsProvider(StaticCredentialsProvider.create(
                AwsBasicCredentials.create(r2Properties.accessKey(), r2Properties.secretKey())))
            .region(Region.of("auto"))
            .build();
    }

    @Bean
    public R2Adapter r2Adapter(S3Client r2Client, S3Presigner r2Presigner, R2Properties r2Properties) {
        return new R2Adapter(r2Client, r2Presigner, r2Properties);
    }
}
