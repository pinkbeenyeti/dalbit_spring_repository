package dalbit.adapter.rest.external.voice.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.voice.dto.request.DeleterVoiceRequest;
import dalbit.adapter.rest.external.voice.dto.request.RegisterVoiceRequest;
import dalbit.adapter.rest.external.voice.dto.request.UpdateVoiceNameRequest;
import dalbit.adapter.rest.external.voice.dto.response.UploadUrlResponse;
import dalbit.adapter.rest.external.voice.dto.response.VoiceResponse;
import dalbit.application.rest.external.voice.useCase.DeleteVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.GetVoiceInfoUseCase;
import dalbit.application.rest.external.voice.useCase.RegisterVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.UpdateVoiceInfoUseCase;
import dalbit.domain.common.storage.Category;
import dalbit.domain.voice.Voice;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dalbit/voice/")
public class VoiceJpaController {

    private final GetVoiceInfoUseCase getVoiceInfoUseCase;
    private final RegisterVoiceUseCase registerVoiceUseCase;
    private final UpdateVoiceInfoUseCase updateVoiceInfoUseCase;
    private final DeleteVoiceUseCase deleteVoiceUseCase;

    @GetMapping("info")
    public ResponseEntity<ApiResponse<List<VoiceResponse>>> getVoiceList(
        @AuthenticationPrincipal Long userId
    ) {
        List<Voice> voiceList = getVoiceInfoUseCase.getVoiceList(userId);

        List<VoiceResponse> responseData = voiceList.stream()
            .map(VoiceResponse::from)
            .toList();

        return ApiResponse.success(responseData);
    }

    @GetMapping("upload-url")
    public ResponseEntity<ApiResponse<UploadUrlResponse>> getUploadUrls(
        @AuthenticationPrincipal Long userId,
        @RequestParam("externalId") String externalId
    ) {
        List<String> uploadUrls = registerVoiceUseCase.getVoiceUploadUrls(
            Category.VOICE,
            externalId,
            10
        );

        return ApiResponse.success(UploadUrlResponse.from(uploadUrls));
    }

    @PostMapping("register")
    public ResponseEntity<ApiResponse<Void>> registerVoice(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody RegisterVoiceRequest request
    ) {
        registerVoiceUseCase.registerVoice(userId, request.name());
        return ApiResponse.success();
    }

    @PatchMapping("update/name")
    public ResponseEntity<ApiResponse<Void>> updateVoiceName(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody UpdateVoiceNameRequest request
    ) {
        updateVoiceInfoUseCase.updateVoiceName(userId, request.externalId(), request.name());
        return ApiResponse.success();
    }

    @DeleteMapping("delete")
    public ResponseEntity<ApiResponse<Void>> deleteVoice(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody DeleterVoiceRequest request
    ) {
        deleteVoiceUseCase.deleteVoice(userId, request.externalId());
        return ApiResponse.success();
    }

}
