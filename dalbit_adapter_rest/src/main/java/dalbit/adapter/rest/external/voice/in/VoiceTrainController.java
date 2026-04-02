package dalbit.adapter.rest.external.voice.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.voice.dto.request.TrainVoiceRequest;
import dalbit.application.rest.external.voice.useCase.TrainVoiceUseCase;
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
@RequestMapping("api/v1/dalbit/voice/train/")
public class VoiceTrainController {

    private final TrainVoiceUseCase trainVoiceUseCase;

    @PostMapping("start")
    public ResponseEntity<ApiResponse<Void>> startVoiceTraining(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody TrainVoiceRequest request
    ) {
        trainVoiceUseCase.startVoiceTraining(userId, request.externalId());
        return ApiResponse.success();
    }
}
