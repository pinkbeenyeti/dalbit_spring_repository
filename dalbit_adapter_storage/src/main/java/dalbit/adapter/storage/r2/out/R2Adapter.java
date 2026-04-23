package dalbit.adapter.storage.r2.out;

import dalbit.adapter.storage.r2.property.R2Properties;
import dalbit.application.storage.port.DeleteStoragePort;
import dalbit.application.storage.port.GenerateUploadUrlPort;
import dalbit.application.storage.port.VerifyUploadPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Delete;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectsRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.ObjectIdentifier;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Slf4j
@RequiredArgsConstructor
public class R2Adapter implements GenerateUploadUrlPort, VerifyUploadPort, DeleteStoragePort {

    private static final int S3_BATCH_SIZE = 1000;

    private final S3Client r2Client;
    private final S3Presigner r2Presigner;
    private final R2Properties r2Properties;

    @Override
    public void deleteFile(String filePath) {
        execute(() -> {
            DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(r2Properties.bucketName())
                .key(filePath)
                .build();

            r2Client.deleteObject(request);

            log.info("[R2 Storage] 파일 삭제 성공: {}", filePath);
            return null;
        }, "파일 삭제 중 에러 발생: " + filePath);
    }

    @Override
    public void deleteFiles(List<String> filePaths) {
        if (filePaths == null || filePaths.isEmpty()) return;

        List<ObjectIdentifier> objects = filePaths.stream()
            .filter(path -> path != null && !path.isBlank())
            .map(path -> ObjectIdentifier.builder().key(path).build())
            .toList();

        if (objects.isEmpty()) return;

        execute(() -> {
            deleteInBatches(objects);
            log.info("[R2 Storage] 파일 {}개 삭제 완료", objects.size());
            return null;
        }, "파일 일괄 삭제 중 에러 발생");
    }

    @Override
    public void deleteDirectories(List<String> directoryPaths) {
        if (directoryPaths == null || directoryPaths.isEmpty()) return;
        directoryPaths.forEach(this::deleteDirectory);
    }

    @Override
    public void deleteDirectory(String directoryPath) {
        if (isInvalidDirectoryPath(directoryPath)) {
            log.warn("[R2 Storage] 유효하지 않은 디렉터리 삭제 요청 거부: {}", directoryPath);
            return;
        }

        execute(() -> {
            String prefix = directoryPath.endsWith("/") ? directoryPath : directoryPath + "/";
            int totalDeleted = deleteAllObjectsWithPrefix(prefix);
            log.info("[R2 Storage] 디렉터리 삭제 완료 (파일 {}개): {}", totalDeleted, prefix);
            return null;
        }, "디렉터리 삭제 중 에러 발생: " + directoryPath);
    }

    private int deleteAllObjectsWithPrefix(String prefix) {
        String continuationToken = null;
        int totalDeleted = 0;

        do {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(r2Properties.bucketName())
                .prefix(prefix)
                .continuationToken(continuationToken)
                .build();

            ListObjectsV2Response listResponse = r2Client.listObjectsV2(listRequest);
            List<ObjectIdentifier> objects = listResponse.contents().stream()
                .map(s3Object -> ObjectIdentifier.builder().key(s3Object.key()).build())
                .toList();

            if (!objects.isEmpty()) {
                deleteInBatches(objects);
                totalDeleted += objects.size();
            }
            continuationToken = listResponse.nextContinuationToken();
        } while (continuationToken != null);

        return totalDeleted;
    }

    private void deleteInBatches(List<ObjectIdentifier> objects) {
        for (int i = 0; i < objects.size(); i += S3_BATCH_SIZE) {
            List<ObjectIdentifier> chunk = objects.subList(i, Math.min(i + S3_BATCH_SIZE, objects.size()));

            DeleteObjectsRequest request = DeleteObjectsRequest.builder()
                .bucket(r2Properties.bucketName())
                .delete(Delete.builder().objects(chunk).build())
                .build();

            r2Client.deleteObjects(request);
        }
    }

    private boolean isInvalidDirectoryPath(String path) {
        return path == null || path.trim().isEmpty() || path.trim().equals("/");
    }

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
            return false;
        } catch (S3Exception e) {
            log.error("[R2 Storage] 파일 확인 중 S3 에러 발생: {}", filePath, e);
            throw new DalbitException(ErrorCode.EXTERNAL_STORAGE_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[R2 Storage] 파일 확인 중 알 수 없는 에러 발생: {}", filePath, e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public boolean verifyFileCount(String filePathPrefix, int expectedCount) {
        return execute(() -> {
            ListObjectsV2Request listRequest = ListObjectsV2Request.builder()
                .bucket(r2Properties.bucketName())
                .prefix(filePathPrefix)
                .maxKeys(expectedCount)
                .build();

            long actualCount = r2Client.listObjectsV2(listRequest).contents().stream()
                .filter(s3Object -> !s3Object.key().endsWith("/"))
                .count();

            log.info("[R2 Storage] 파일 개수 검증: {} (예상: {}, 실제: {})", filePathPrefix, expectedCount, actualCount);
            return actualCount >= expectedCount;
        }, "파일 개수 검증 중 에러 발생: " + filePathPrefix);
    }

    @Override
    public List<String> generateUploadUrls(List<String> paths) {
        return paths.stream().map(this::generateSinglePresignedUrl).toList();
    }

    private String generateSinglePresignedUrl(String path) {
        return execute(() -> {
            PutObjectRequest objectRequest = PutObjectRequest.builder()
                .bucket(r2Properties.bucketName())
                .key(path)
                .contentType("audio/wav")
                .build();

            PutObjectPresignRequest presignRequest = PutObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(r2Properties.presignedUrlExpirationMinutes()))
                .putObjectRequest(objectRequest)
                .build();

            return r2Presigner.presignPutObject(presignRequest).url().toString();
        }, "Presigned URL 생성 실패: " + path);
    }

    private <T> T execute(Supplier<T> action, String errorMessage) {
        try {
            return action.get();
        } catch (S3Exception e) {
            log.error("[R2 Storage] {} (S3 Error)", errorMessage, e);
            throw new DalbitException(ErrorCode.EXTERNAL_STORAGE_SERVER_ERROR);
        } catch (Exception e) {
            log.error("[R2 Storage] {} (Unknown Error)", errorMessage, e);
            throw new DalbitException(ErrorCode.INTERNAL_SERVER_ERROR);
        }
    }
}
