package dalbit.adapter.rest.external.userDevice.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.userDevice.dto.request.RegisterUserDeviceRequest;
import dalbit.adapter.rest.external.userDevice.dto.request.UpdateUserDeviceTokenRequest;
import dalbit.application.rest.external.userDevice.useCase.RegisterUserDeviceUseCase;
import dalbit.application.rest.external.userDevice.useCase.UpdateUserDeviceUseCase;
import dalbit.domain.userDevice.OsType;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dalbit/userDevice/")
public class UserDeviceJpaController {

    private final RegisterUserDeviceUseCase registerUserDeviceUseCase;
    private final UpdateUserDeviceUseCase updateUserDeviceUseCase;

    @PostMapping("register")
    public ResponseEntity<ApiResponse<Void>> registerUserDevice(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody RegisterUserDeviceRequest request
    ) {
        registerUserDeviceUseCase.registerUseDevice(
            userId,
            request.deviceUniqueId(),
            request.fcmToken(),
            OsType.valueOf(request.osType())
        );

        return ApiResponse.success();
    }

    @PatchMapping("update/token")
    public ResponseEntity<ApiResponse<Void>> updateToken(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody UpdateUserDeviceTokenRequest request
    ) {
        updateUserDeviceUseCase.updateToken(
            userId,
            request.deviceUniqueId(),
            request.fcmToken()
        );

        return ApiResponse.success();
    }
}
