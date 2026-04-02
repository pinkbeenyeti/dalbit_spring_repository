package dalbit.adapter.rest.external.voice.dto.response;

import java.util.List;

public record UploadUrlResponse(
    List<String> uploadUrls
) {
    public static UploadUrlResponse from(List<String> urls) {
        return new UploadUrlResponse(urls); // Java 16 이상 record의 기본 생성자 활용
    }
}
