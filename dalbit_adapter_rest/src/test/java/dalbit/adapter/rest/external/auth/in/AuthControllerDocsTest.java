package dalbit.adapter.rest.external.auth.in;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.auth.dto.request.RefreshTokenRequest;
import dalbit.adapter.rest.external.auth.dto.request.SocialLoginRequest;
import dalbit.application.rest.external.auth.dto.SocialLoginResult;
import dalbit.application.rest.external.auth.dto.TokenResult;
import dalbit.application.rest.external.auth.useCase.LoginUseCase;
import dalbit.application.rest.external.auth.useCase.ReissueTokenUseCase;
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

@WebMvcTest(AuthController.class)
@AutoConfigureRestDocs
class AuthControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private LoginUseCase loginUseCase;
    @MockitoBean private ReissueTokenUseCase reissueTokenUseCase;

    @Test
    @WithMockUser
    @DisplayName("소셜 로그인 API 명세서 - 성공")
    void socialLogin() throws Exception {
        SocialLoginRequest request = new SocialLoginRequest("KAKAO", "oauth-social-token");
        SocialLoginResult result = new SocialLoginResult("user-external-id", "spring-access-token", "spring-refresh-token");

        given(loginUseCase.socialLogin(anyString(), anyString())).willReturn(result);

        mockMvc.perform(post("/auth/login")
            .with(csrf())
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("auth-login",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("provider").description("소셜 로그인 제공자 (예: KAKAO, GOOGLE)"),
                    fieldWithPath("token").description("소셜 인증 서버로부터 받은 토큰")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드"),
                    fieldWithPath("message").description("응답 메시지").optional(),
                    fieldWithPath("data.userExternalId").description("유저의 식별자 아이디"),
                    fieldWithPath("data.accessToken").description("발급된 JWT 액세스 토큰"),
                    fieldWithPath("data.refreshToken").description("발급된 JWT 리프레시 토큰")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("소셜 로그인 API 명세서 - 실패, 필수 입력값 누락 (400 Bad Request)")
    void socialLogin_Fail_InvalidInput() throws Exception {
        SocialLoginRequest invalidRequest = new SocialLoginRequest(null, "");

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andDo(document("auth-login-fail-invalid-input",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 상태 코드 (예: 400)"),
                    fieldWithPath("message").description("에러 상세 메시지 (예: 제공자 정보는 필수입니다.)")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("소셜 로그인 API 명세서 - 실패, 유효하지 않은 Provider 토큰 (401 Unauthorized)")
    void socialLogin_Fail_InvalidToken() throws Exception {
        SocialLoginRequest request = new SocialLoginRequest("KAKAO", "invalid-token-123");

        given(loginUseCase.socialLogin(anyString(), anyString()))
            .willThrow(new DalbitException(ErrorCode.INVALID_PROVIDER_TOKEN));

        mockMvc.perform(post("/auth/login")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isUnauthorized())
            .andDo(document("auth-login-fail-invalid-token",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 상태 코드 (예: 401)"),
                    fieldWithPath("message").description("유효하지 않은 소셜 토큰입니다.")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("토큰 재발급 API 명세서 - 성공")
    void refreshTokens() throws Exception {
        RefreshTokenRequest request = new RefreshTokenRequest("spring-refresh-token");
        TokenResult tokenResult = new TokenResult("spring-new-access-token", "spring-new-refresh-token");

        given(reissueTokenUseCase.reissue(anyString())).willReturn(tokenResult);

        mockMvc.perform(post("/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("auth-refresh",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("refreshToken").description("기존에 발급받은 리프레시 토큰")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 상태 코드"),
                    fieldWithPath("message").description("응답 메시지").optional(),
                    fieldWithPath("data.accessToken").description("새로 발급된 JWT 액세스 토큰"),
                    fieldWithPath("data.refreshToken").description("새로 발급된 JWT 리프레시 토큰")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("토큰 재발급 실패 - 유효하지 않은 리프레시 토큰 (401 Unauthorized)")
    void refreshTokens_Fail_InvalidToken() throws Exception {
        RefreshTokenRequest invalidRequest = new RefreshTokenRequest("invalid-refresh-token-123");

        given(reissueTokenUseCase.reissue(anyString()))
            .willThrow(new DalbitException(ErrorCode.INVALID_REFRESH_TOKEN));

        mockMvc.perform(post("/auth/refresh")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isUnauthorized())
            .andDo(document("auth-refresh-fail-invalid-token",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 상태 코드 (예: -20001)"),
                    fieldWithPath("message").description("유효하지 않은 토큰입니다.")
                )
            ));
    }
}