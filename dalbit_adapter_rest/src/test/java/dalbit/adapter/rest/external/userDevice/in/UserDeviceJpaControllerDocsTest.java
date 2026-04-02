package dalbit.adapter.rest.external.userDevice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.userDevice.dto.request.RegisterUserDeviceRequest;
import dalbit.adapter.rest.external.userDevice.dto.request.UpdateUserDeviceTokenRequest;
import dalbit.application.rest.external.userDevice.useCase.RegisterUserDeviceUseCase;
import dalbit.application.rest.external.userDevice.useCase.UpdateUserDeviceUseCase;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserDeviceJpaController.class)
@AutoConfigureRestDocs
class UserDeviceJpaControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private RegisterUserDeviceUseCase registerUserDeviceUseCase;
    @MockitoBean private UpdateUserDeviceUseCase updateUserDeviceUseCase;


    @Test
    @WithMockUser
    @DisplayName("유저 기기 등록 - 성공")
    void registerUserDevice_Success() throws Exception {
        RegisterUserDeviceRequest request = new RegisterUserDeviceRequest(
            "DEVICE-UUID-1234",
            "fcm-token-abcde-12345",
            "AOS"
        );

        mockMvc.perform(post("/api/v1/dalbit/userDevice/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("user-device-register-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("deviceUniqueId").description("기기 고유 식별자 (UUID 등)"),
                    fieldWithPath("fcmToken").description("발급받은 FCM 푸시 토큰"),
                    fieldWithPath("osType").description("운영체제 타입 (예: AOS, IOS)")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("유저 기기 등록 - 실패 (입력값 누락)")
    void registerUserDevice_Fail_InvalidInput() throws Exception {
        RegisterUserDeviceRequest request = new RegisterUserDeviceRequest(
            "DEVICE-UUID-1234",
            "",
            "ANDROID"
        );

        mockMvc.perform(post("/api/v1/dalbit/userDevice/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("user-device-register-fail-input",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("FCM 토큰은 필수 입력값입니다. (예시)")
                )
            ));
    }


    @Test
    @WithMockUser
    @DisplayName("FCM 토큰 업데이트 - 성공")
    void updateToken_Success() throws Exception {
        UpdateUserDeviceTokenRequest request = new UpdateUserDeviceTokenRequest(
            "DEVICE-UUID-1234",
            "new-fcm-token-xyz-98765"
        );

        mockMvc.perform(patch("/api/v1/dalbit/userDevice/update/token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("user-device-update-token-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("deviceUniqueId").description("기기 고유 식별자"),
                    fieldWithPath("fcmToken").description("새로 갱신된 FCM 푸시 토큰")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("FCM 토큰 업데이트 - 실패 (등록되지 않은 기기)")
    void updateToken_Fail_NotExist() throws Exception {
        UpdateUserDeviceTokenRequest request = new UpdateUserDeviceTokenRequest(
            "UNKNOWN-DEVICE-ID",
            "new-fcm-token"
        );

        doThrow(new DalbitException(ErrorCode.NOT_EXIST_USER_DEVICE))
            .when(updateUserDeviceUseCase).updateToken(any(), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/dalbit/userDevice/update/token")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("user-device-update-token-fail-not-exist",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("존재하지 않는 사용자 기기입니다.")
                )
            ));
    }
}