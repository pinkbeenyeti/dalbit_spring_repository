package dalbit.adapter.rest.external.user.in;

import dalbit.adapter.rest.common.response.ApiResponse;
import dalbit.adapter.rest.external.user.dto.request.UpdateUserNameRequest;
import dalbit.adapter.rest.external.user.dto.response.UserResponse;
import dalbit.application.rest.external.user.useCase.GetUserInfoUseCase;
import dalbit.application.rest.external.user.useCase.UpdateUserUseCae;
import dalbit.domain.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/dalbit/user/")
public class UserJpaController {

    private final UpdateUserUseCae updateUserUseCae;
    private final GetUserInfoUseCase getUserInfoUseCase;

    @GetMapping("info")
    public ResponseEntity<ApiResponse<UserResponse>> getUserInfo(
        @AuthenticationPrincipal Long userId
    ) {
        User user = getUserInfoUseCase.getUserInfo(userId);

        UserResponse responseData = UserResponse.from(user);

        return ApiResponse.success(responseData);
    }

    @PatchMapping("update/name")
    public ResponseEntity<ApiResponse<Void>> updateUserName(
        @AuthenticationPrincipal Long userId,
        @Valid @RequestBody UpdateUserNameRequest request
    ) {
        updateUserUseCae.updateUserName(userId, request.name());

        return ApiResponse.success();
    }
}
