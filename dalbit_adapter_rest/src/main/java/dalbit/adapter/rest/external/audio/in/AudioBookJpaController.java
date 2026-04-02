package dalbit.adapter.rest.external.audio.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.audio.dto.request.DeleteAudioBookRequest;
import dalbit.adapter.rest.external.audio.dto.response.AudioBookResponse;
import dalbit.application.persistence.jpa.audio.dto.AudioBookResult;
import dalbit.application.rest.external.audio.useCase.DeleteAudioBookUseCase;
import dalbit.application.rest.external.audio.useCase.GetAudioBooksUseCase;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dalbit/audio/")
public class AudioBookJpaController {

    private final GetAudioBooksUseCase getAudioBooksUseCase;
    private final DeleteAudioBookUseCase deleteAudioBookUseCase;

    @GetMapping("info")
    public ResponseEntity<ApiResponse<List<AudioBookResponse>>> getAudioBookList(
        @AuthenticationPrincipal Long userId
    ) {
        List<AudioBookResult> audioBookList = getAudioBooksUseCase.getAudioBooks(userId);

        List<AudioBookResponse> responseData = audioBookList.stream()
            .map(AudioBookResponse::from)
            .toList();

        return ApiResponse.success(responseData);
    }


    @DeleteMapping("delete")
    public ResponseEntity<ApiResponse<Void>> deleteAudioBook(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody DeleteAudioBookRequest request
    ) {
        deleteAudioBookUseCase.deleteAudioBook(userId, request.externalId());
        return ApiResponse.success();
    }

}
