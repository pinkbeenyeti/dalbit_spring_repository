package dalbit.adapter.rest.external.audio.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.audio.dto.request.GenerateAudioBookRequest;
import dalbit.application.rest.external.audio.useCase.GenerateAudioBookUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dalbit/audio/generate/")
public class AudioBookGenerateController {

    private final GenerateAudioBookUseCase generateAudioBookUseCase;

    @PostMapping("start")
    ResponseEntity<ApiResponse<Void>> startAudioBookGenerate(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody GenerateAudioBookRequest request
    ) {
        generateAudioBookUseCase.startGenerateAudioBook(userId, request.fairytaleId(), request.voiceExternalId());
        return ApiResponse.success();
    }
}
