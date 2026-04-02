package dalbit.adapter.rest.external.voice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.voice.dto.request.TrainVoiceRequest;
import dalbit.application.rest.external.voice.useCase.TrainVoiceUseCase;
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
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VoiceTrainController.class)
@AutoConfigureRestDocs
class VoiceTrainControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private TrainVoiceUseCase trainVoiceUseCase;


    @Test
    @WithMockUser
    @DisplayName("음성 학습 시작 요청 - 성공")
    void startVoiceTraining_Success() throws Exception {
        TrainVoiceRequest request = new TrainVoiceRequest(UUID.randomUUID().toString());

        mockMvc.perform(post("/api/v1/dalbit/voice/train/start")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("voice-train-start-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("externalId").description("학습을 시작할 음성 모델의 고유 식별자")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 학습 시작 요청 - 실패 (존재하지 않는 음성)")
    void startVoiceTraining_Fail_NotExist() throws Exception {
        TrainVoiceRequest request = new TrainVoiceRequest("UNKNOWN-VOICE-ID");

        doThrow(new DalbitException(ErrorCode.NOT_EXIST_VOICE))
            .when(trainVoiceUseCase).startVoiceTraining(any(), anyString());

        mockMvc.perform(post("/api/v1/dalbit/voice/train/start")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("voice-train-start-fail-not-exist",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("존재하지 않는 음성 데이터입니다.")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("음성 학습 시작 요청 - 실패 (파일 업로드 미완료)")
    void startVoiceTraining_Fail_IncompleteUpload() throws Exception {
        TrainVoiceRequest request = new TrainVoiceRequest(UUID.randomUUID().toString());

        doThrow(new DalbitException(ErrorCode.INCOMPLETE_VOICE_UPLOAD))
            .when(trainVoiceUseCase).startVoiceTraining(any(), anyString());

        mockMvc.perform(post("/api/v1/dalbit/voice/train/start")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("voice-train-start-fail-incomplete",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("음성 파일 10개가 모두 업로드되지 않았습니다.")
                )
            ));
    }
}