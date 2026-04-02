package dalbit.adapter.rest.external.device.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.device.dto.request.RegisterDeviceOwnerRequest;
import dalbit.adapter.rest.external.device.dto.request.UpdateDeviceNameRequest;
import dalbit.adapter.rest.external.device.dto.response.DeviceResponse;
import dalbit.application.rest.external.device.useCase.GetDevicesUseCase;
import dalbit.application.rest.external.device.useCase.RegisterDeviceUseCase;
import dalbit.application.rest.external.device.useCase.UpdateDeviceUseCase;
import dalbit.domain.device.Device;
import dalbit.domain.device.DeviceName;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dalbit/device/")
public class DeviceJpaController {

    private final GetDevicesUseCase getDevicesUseCase;
    private final RegisterDeviceUseCase registerDeviceUseCase;
    private final UpdateDeviceUseCase updateDeviceUseCase;

    @GetMapping("myDevices")
    public ResponseEntity<ApiResponse<List<DeviceResponse>>> getMyDevices(
        @AuthenticationPrincipal Long userId
    ) {
        List<Device> devices = getDevicesUseCase.getAllDevices(userId);

        List<DeviceResponse> responseData = devices.stream()
            .map(DeviceResponse::from)
            .toList();

        return ApiResponse.success(responseData);
    }

    @PostMapping("register/owner")
    public ResponseEntity<ApiResponse<Void>> registerDeviceOwner(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody RegisterDeviceOwnerRequest request

    ) {
        registerDeviceUseCase.registerDeviceOwner(request.serialNumber(), userId);
        return ApiResponse.success();
    }

    @PatchMapping("update/name")
    public ResponseEntity<ApiResponse<Void>> updateDeviceName(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody UpdateDeviceNameRequest request
    ) {
        updateDeviceUseCase.updateDeviceName(
            userId,
            request.serialNumber(),
            DeviceName.of(request.newName())
        );

        return ApiResponse.success();
    }
}
