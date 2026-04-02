package dalbit.adapter.rest.external.audio.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.audio.dto.request.DeleteAudioBookRequest;
import dalbit.application.persistence.jpa.audio.dto.AudioBookResult;
import dalbit.application.rest.external.audio.useCase.DeleteAudioBookUseCase;
import dalbit.application.rest.external.audio.useCase.GetAudioBooksUseCase;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AudioBookJpaController.class)
@AutoConfigureRestDocs
class AudioBookJpaControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GetAudioBooksUseCase getAudioBooksUseCase;
    @MockitoBean private DeleteAudioBookUseCase deleteAudioBookUseCase;


    @Test
    @WithMockUser
    @DisplayName("내 오디오북 목록 조회 - 성공")
    void getAudioBookList_Success() throws Exception {
        AudioBookResult mockResult = mock(AudioBookResult.class);
        given(mockResult.externalId()).willReturn(UUID.randomUUID().toString());
        given(mockResult.voiceExternalId()).willReturn("VOICE-UUID-1234");
        given(mockResult.fairytaleId()).willReturn(1L);
        given(mockResult.status()).willReturn(dalbit.domain.audio.GenerationStatus.COMPLETED);
        given(mockResult.audioUrl()).willReturn("https://s3.amazonaws.com/dalbit/audio/sample.mp3");
        given(getAudioBooksUseCase.getAudioBooks(any())).willReturn(List.of(mockResult));

        mockMvc.perform(get("/api/v1/dalbit/audio/info")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("audio-get-list-success",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data[].audioBookExternalId").description("오디오북 고유 식별자"),
                    fieldWithPath("data[].voiceExternalId").description("사용한 음성의 고유 식별자"),
                    fieldWithPath("data[].fairytaleId").description("원작 동화의 고유 ID"),
                    fieldWithPath("data[].status").description("오디오북 생성 상태 (예: 생성중, 완료)"),
                    fieldWithPath("data[].audioUrl").description("완성된 오디오북 파일 URL (S3 등)")
                )
            ));
    }


    @Test
    @WithMockUser
    @DisplayName("오디오북 삭제 - 성공")
    void deleteAudioBook_Success() throws Exception {
        DeleteAudioBookRequest request = new DeleteAudioBookRequest(UUID.randomUUID().toString());

        mockMvc.perform(delete("/api/v1/dalbit/audio/delete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("audio-delete-success",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                requestFields(
                    fieldWithPath("externalId").description("삭제할 오디오북의 고유 식별자")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("오디오북 삭제 - 실패 (식별자 누락)")
    void deleteAudioBook_Fail_InvalidInput() throws Exception {
        DeleteAudioBookRequest request = new DeleteAudioBookRequest("");

        mockMvc.perform(delete("/api/v1/dalbit/audio/delete")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest())
            .andDo(document("audio-delete-fail-input",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("오디오북 식별자는 필수 입력값입니다.")
                )
            ));
    }
}