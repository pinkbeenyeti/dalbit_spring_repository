package dalbit.adapter.rest.external.user.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.user.dto.request.UpdateUserNameRequest;
import dalbit.application.rest.external.user.useCase.GetUserInfoUseCase;
import dalbit.application.rest.external.user.useCase.UpdateUserUseCae;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.user.Role;
import dalbit.domain.user.User;
import dalbit.domain.user.UserEmail;
import dalbit.domain.user.UserName;
import java.util.UUID;
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
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserJpaController.class)
@AutoConfigureRestDocs
class UserJpaControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GetUserInfoUseCase getUserInfoUseCase;
    @MockitoBean private UpdateUserUseCae updateUserUseCae;

    @Test
    @WithMockUser
    @DisplayName("유저 정보 조회 - 성공")
    void getUserInfo_Success() throws Exception {
        User mockUser = User.builder()
            .id(1L)
            .externalId(UUID.randomUUID().toString())
            .providerId("social-123")
            .name(UserName.of("달빛지기"))
            .email(UserEmail.of("dalbit@example.com"))
            .role(Role.USER)
            .build();

        given(getUserInfoUseCase.getUserInfo(any())).willReturn(mockUser);

        mockMvc.perform(get("/api/v1/dalbit/user/info")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("user-get-info-success",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.externalId").description("유저 고유 식별자"),
                    fieldWithPath("data.name").description("유저 이름"),
                    fieldWithPath("data.email").description("유저 이메일")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("유저 정보 조회 - 실패, 유저 없음 (400 Bad Request)")
    void getUserInfo_Fail_NotFound() throws Exception {
        given(getUserInfoUseCase.getUserInfo(any()))
            .willThrow(new DalbitException(ErrorCode.NOT_EXIST_USER));

        mockMvc.perform(get("/api/v1/dalbit/user/info")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isBadRequest())
            .andDo(document("user-get-info-fail",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("존재하지 않는 사용자입니다.")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("유저 이름 수정 - 성공")
    void updateUserName_Success() throws Exception {
        UpdateUserNameRequest request = new UpdateUserNameRequest("새이름");

        mockMvc.perform(patch("/api/v1/dalbit/user/update/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("user-update-name-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("name").description("변경할 유저 이름")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("유저 이름 수정 실패 - 잘못된 입력값 (400 Bad Request)")
    void updateUserName_Fail_InvalidInput() throws Exception {
        UpdateUserNameRequest invalidRequest = new UpdateUserNameRequest("");

        mockMvc.perform(patch("/api/v1/dalbit/user/update/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andDo(document("user-update-name-fail-input",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("이름은 필수 입력값입니다.")
                )
            ));
    }
}