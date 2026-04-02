package dalbit.adapter.storage.s3.out;

import dalbit.adapter.storage.s3.property.S3Properties;
import dalbit.application.storage.port.GenerateUploadUrlPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@RequiredArgsConstructor
public class S3Adapter implements GenerateUploadUrlPort {

    private final S3Presigner s3Presigner;
    private final S3Properties s3Properties;

    @Override
    public List<String> generateUploadUrls(List<String> paths) {
        return paths.stream()
            .map(this::generateSinglePresignedUrl)
            .toList();
    }

    private String generateSinglePresignedUrl(String path) {
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(s3Properties.bucketName())
                .key(path)
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(s3Properties.presignedUrlExpirationMinutes()))
                .putObjectRequest(objectRequest)
                .build();

            PresignedPutObjectRequest presignedRequest = s3Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();

        } catch (Exception e) {
            log.error("[S3 Storage] Presigned URL 생성 실패. path: {}", path, e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
