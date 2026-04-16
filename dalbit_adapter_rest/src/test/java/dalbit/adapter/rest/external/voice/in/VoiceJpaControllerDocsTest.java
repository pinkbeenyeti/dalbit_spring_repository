package dalbit.adapter.rest.external.voice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.voice.dto.request.DeleterVoiceRequest;
import dalbit.adapter.rest.external.voice.dto.request.RegisterVoiceRequest;
import dalbit.adapter.rest.external.voice.dto.request.UpdateVoiceNameRequest;
import dalbit.application.rest.external.voice.useCase.DeleteVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.GetVoiceInfoUseCase;
import dalbit.application.rest.external.voice.useCase.RegisterVoiceUseCase;
import dalbit.application.rest.external.voice.useCase.UpdateVoiceInfoUseCase;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.voice.RegistrationStatus;
import dalbit.domain.voice.Voice;
import dalbit.domain.voice.VoiceName;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoiceJpaController.class)
@AutoConfigureRestDocs
class VoiceJpaControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GetVoiceInfoUseCase getVoiceInfoUseCase;
    @MockitoBean private RegisterVoiceUseCase registerVoiceUseCase;
    @MockitoBean private UpdateVoiceInfoUseCase updateVoiceInfoUseCase;
    @MockitoBean private DeleteVoiceUseCase deleteVoiceUseCase;

    @Test
    @WithMockUser
    @DisplayName("내 음성 목록 조회 - 성공")
    void getVoiceList_Success() throws Exception {
        Voice mockVoice = Voice.builder()
            .id(1L)
            .externalId(UUID.randomUUID().toString())
            .name(VoiceName.of("나의 첫번째 목소리"))
            .build();

        given(getVoiceInfoUseCase.getVoiceList(any())).willReturn(List.of(mockVoice));

        mockMvc.perform(get("/api/v1/dalbit/voice/info")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("voice-get-list-success",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data[].externalId").description("음성 고유 식별자"),
                    fieldWithPath("data[].name").description("음성 이름"),
                    fieldWithPath("data[].status").description("음성 등록 상태")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 업로드 URL 발급 - 성공")
    void getUploadUrls_Success() throws Exception {
        String externalId = UUID.randomUUID().toString();
        List<String> mockUrls = List.of(
            "https://s3.amazonaws.com/dalbit/voice/" + externalId + "/1?presigned=...",
            "https://s3.amazonaws.com/dalbit/voice/" + externalId + "/2?presigned=..."
        );

        given(registerVoiceUseCase.getVoiceUploadUrls(any(), anyString(), anyInt())).willReturn(mockUrls);

        mockMvc.perform(get("/api/v1/dalbit/voice/upload-url")
                .param("externalId", externalId)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("voice-get-upload-url-success",
                preprocessResponse(prettyPrint()),
                queryParameters(
                    parameterWithName("externalId").description("업로드할 음성 데이터의 대상 ID")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.uploadUrls[]").description("발급된 S3 Presigned URL 목록")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 등록 - 성공")
    void registerVoice_Success() throws Exception {
        RegisterVoiceRequest request = new RegisterVoiceRequest("달빛 자장가");
        Voice mockVoice = Voice.builder()
            .id(1L)
            .externalId(UUID.randomUUID().toString())
            .name(VoiceName.of("달빛 자장가"))
            .status(RegistrationStatus.WAITING_UPLOAD)
            .build();

        given(registerVoiceUseCase.registerVoice(any(), anyString())).willReturn(mockVoice);

        mockMvc.perform(post("/api/v1/dalbit/voice/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("voice-register-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("name").description("등록할 음성의 이름")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data.externalId").description("등록된 음성 고유 식별자"),
                    fieldWithPath("data.name").description("등록된 음성 이름"),
                    fieldWithPath("data.status").description("등록된 음성 상태")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 등록 - 실패 (이미 존재하는 이름)")
    void registerVoice_Fail_AlreadyExist() throws Exception {
        RegisterVoiceRequest request = new RegisterVoiceRequest("달빛 자장가");

        doThrow(new DalbitException(ErrorCode.ALREADY_EXIST_VOICE_NAME))
            .when(registerVoiceUseCase).registerVoice(any(), anyString());

        mockMvc.perform(post("/api/v1/dalbit/voice/register")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andDo(document("voice-register-fail-already-exist",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("이미 존재하는 음성 이름입니다.")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 이름 수정 - 성공")
    void updateVoiceName_Success() throws Exception {
        UpdateVoiceNameRequest request = new UpdateVoiceNameRequest(UUID.randomUUID().toString(), "새로운 자장가");

        mockMvc.perform(patch("/api/v1/dalbit/voice/update/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("voice-update-name-success",
                preprocessRequest(prettyPrint()),
                requestFields(
                    fieldWithPath("externalId").description("수정할 음성 식별자"),
                    fieldWithPath("name").description("새로운 이름")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 이름 수정 - 실패 (존재하지 않는 음성)")
    void updateVoiceName_Fail_NotExist() throws Exception {
        UpdateVoiceNameRequest request = new UpdateVoiceNameRequest("WRONG-ID", "새로운 자장가");

        doThrow(new DalbitException(ErrorCode.NOT_EXIST_VOICE))
            .when(updateVoiceInfoUseCase).updateVoiceName(any(), anyString(), anyString());

        mockMvc.perform(patch("/api/v1/dalbit/voice/update/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("voice-update-name-fail-not-exist",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("존재하지 않는 음성입니다.")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 삭제 - 성공")
    void deleteVoice_Success() throws Exception {
        DeleterVoiceRequest request = new DeleterVoiceRequest(UUID.randomUUID().toString());

        mockMvc.perform(delete("/api/v1/dalbit/voice/delete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("voice-delete-success",
                preprocessRequest(prettyPrint()),
                requestFields(
                    fieldWithPath("externalId").description("삭제할 음성 식별자")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }
}