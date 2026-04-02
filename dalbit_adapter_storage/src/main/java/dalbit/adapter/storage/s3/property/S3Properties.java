package dalbit.adapter.storage.s3.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "storage.s3")
public record S3Properties(
    String region,
    String bucketName,
    Long presignedUrlExpirationMinutes
) {

}
