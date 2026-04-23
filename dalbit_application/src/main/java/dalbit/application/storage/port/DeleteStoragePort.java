package dalbit.application.storage.port;

import java.util.List;

public interface DeleteStoragePort {
    void deleteFile(String filePath);
    void deleteDirectory(String directoryPath);
    void deleteFiles(List<String> filePaths);
    void deleteDirectories(List<String> directoryPaths);
}