package dalbit.application.storage.event;

import java.util.List;
import java.util.Objects;

public record StorageDeleteEvent(
    List<String> filePaths,
    List<String> directoryPaths
) {
    public StorageDeleteEvent {
        filePaths = Objects.requireNonNullElse(filePaths, List.of());
        directoryPaths = Objects.requireNonNullElse(directoryPaths, List.of());
    }

    public static StorageDeleteEvent ofFile(String filePath) {
        return new StorageDeleteEvent(List.of(filePath), List.of());
    }

    public static StorageDeleteEvent ofDirectory(String directoryPath) {
        return new StorageDeleteEvent(List.of(), List.of(directoryPath));
    }

    public static StorageDeleteEvent ofFiles(List<String> filePaths) {
        return new StorageDeleteEvent(filePaths, List.of());
    }

    public static StorageDeleteEvent ofDirectories(List<String> directoryPaths) {
        return new StorageDeleteEvent(List.of(), directoryPaths);
    }

    public boolean isEmpty() {
        return filePaths.isEmpty() && directoryPaths.isEmpty();
    }
}
