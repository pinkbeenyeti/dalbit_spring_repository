package dalbit.application.storage.port;

public interface VerifyUploadPort {
    boolean existsFile(String filePath);
    boolean verifyFileCount(String filePathPrefix, int expectedCount);
}
