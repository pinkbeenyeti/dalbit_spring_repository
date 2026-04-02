package dalbit.adapter.storage.s3.config;

import dalbit.adapter.storage.s3.out.S3Adapter;
import dalbit.adapter.storage.s3.property.S3Properties;
import dalbit.application.storage.port.GenerateUploadUrlPort;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;

@Configuration
@ConditionalOnProperty(name = "storage.provider", havingValue = "s3")
public class S3Config {


    @Bean
    public S3Presigner s3Presigner(S3Properties s3Properties) {
        return S3Presigner.builder()
            .region(Region.of(s3Properties.region()))
            .build();
    }

    @Bean
    public GenerateUploadUrlPort s3FileStorageAdapter(S3Presigner s3Presigner, S3Properties s3Properties) {
        return new S3Adapter(s3Presigner, s3Properties);
    }
}
