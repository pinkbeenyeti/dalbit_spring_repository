package dalbit.adapter.storage.r2.out;

import dalbit.adapter.storage.r2.property.R2Properties;
import dalbit.application.storage.port.GenerateUploadUrlPort;
import dalbit.application.storage.port.VerifyUploadPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@RequiredArgsConstructor
public class R2Adapter implements GenerateUploadUrlPort, VerifyUploadPort {

    private final S3Client r2Client;
    private final S3Presigner r2Presigner;
    private final R2Properties r2Properties;

    @Override
    public boolean existsFile(String filePath) {
        try {
            HeadObjectRequest request = HeadObjectRequest.builder()
                .bucket(r2Properties.bucketName())
                .key(filePath)
                .build();

            r2Client.headObject(request);
            return true;

        } catch (NoSuchKeyException e) {
            log.warn("[R2 Storage] 파일을 찾을 수 없습니다. key: {}", filePath);
            return false;
        } catch (S3Exception e) {
            log.error("[R2 Storage] R2 서버 통신 중 에러 발생. key: {}", filePath, e);
            throw new DalbitException(ErrorCode.EXTERNAL_STORAGE_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[R2 Storage] R2 서버 통신 중 알 수없는 에러가 발생하였습니다. ", e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean verifyFileCount(String filePathPrefix, int expectedCount) {
        try {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(r2Properties.bucketName())
                .prefix(filePathPrefix)
                .maxKeys(expectedCount) // Performance Optimization: Fetch only up to expectedCount
                .build();

            long actualCount = r2Client.listObjectsV2(listRequest).contents().stream()
                .filter(s3Object -> !s3Object.key().endsWith("/"))
                .count();

            log.info("[R2 Storage] 경로({}) 파일 개수 확인: 요청 {}개 / 실제 {}개", filePathPrefix, expectedCount, actualCount);
            return actualCount >= expectedCount;

        } catch (S3Exception e) {
            log.error("[R2 Storage] 디렉터리 파일 목록 조회 중 에러 발생. prefix: {}", filePathPrefix, e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public List<String> generateUploadUrls(List<String> paths) {
        return paths.stream()
            .map(this::generateSinglePresignedUrl)
            .toList();
    }

    private String generateSinglePresignedUrl(String path) {
        try {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(r2Properties.bucketName())
                .key(path)
                .contentType("audio/wav")
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(r2Properties.presignedUrlExpirationMinutes()))
                .putObjectRequest(objectRequest)
                .build();

            PresignedPutObjectRequest presignedRequest = r2Presigner.presignPutObject(presignRequest);
            return presignedRequest.url().toString();

        } catch (Exception e) {
            log.error("[R2 Storage] Presigned URL 생성 실패. Path: {}", path, e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
