package dalbit.application.storage.port;

import java.util.List;

public interface GenerateUploadUrlPort {
    List<String> generateUploadUrls(List<String> paths);
}
