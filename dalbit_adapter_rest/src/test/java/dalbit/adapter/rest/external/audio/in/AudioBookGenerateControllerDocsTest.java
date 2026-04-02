package dalbit.adapter.rest.external.audio.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.audio.dto.request.GenerateAudioBookRequest;
import dalbit.application.rest.external.audio.useCase.GenerateAudioBookUseCase;
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

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AudioBookGenerateController.class)
@AutoConfigureRestDocs
class AudioBookGenerateControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GenerateAudioBookUseCase generateAudioBookUseCase;


    @Test
    @WithMockUser
    @DisplayName("오디오북 생성 시작 요청 - 성공")
    void startAudioBookGenerate_Success() throws Exception {
        GenerateAudioBookRequest request = new GenerateAudioBookRequest(
            1L,
            UUID.randomUUID().toString()
        );

        mockMvc.perform(post("/api/v1/dalbit/audio/generate/start")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("audio-generate-start-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("fairytaleId").description("오디오북으로 만들 동화의 고유 ID"),
                    fieldWithPath("voiceExternalId").description("오디오북에 적용할 학습된 음성 식별자")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("오디오북 생성 시작 요청 - 실패 (존재하지 않는 음성)")
    void startAudioBookGenerate_Fail_NotExistVoice() throws Exception {
        GenerateAudioBookRequest request = new GenerateAudioBookRequest(1L, "WRONG-VOICE-ID");

        doThrow(new DalbitException(ErrorCode.NOT_EXIST_VOICE))
            .when(generateAudioBookUseCase).startGenerateAudioBook(any(), anyLong(), anyString());

        mockMvc.perform(post("/api/v1/dalbit/audio/generate/start")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("audio-generate-start-fail-not-exist-voice",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("존재하지 않는 음성 데이터입니다.")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("오디오북 생성 시작 요청 - 실패 (존재하지 않는 동화)")
    void startAudioBookGenerate_Fail_NotExistFairytale() throws Exception {
        GenerateAudioBookRequest request = new GenerateAudioBookRequest(999L, UUID.randomUUID().toString());

        doThrow(new DalbitException(ErrorCode.NOT_EXIST_FAIRYTALE))
            .when(generateAudioBookUseCase).startGenerateAudioBook(any(), anyLong(), anyString());

        mockMvc.perform(post("/api/v1/dalbit/audio/generate/start")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("audio-generate-start-fail-not-exist-fairytale",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("존재하지 않는 동화입니다.")
                )
            ));
    }
}