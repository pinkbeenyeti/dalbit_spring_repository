package dalbit.adapter.rest.external.fairytale.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.application.rest.external.fairytale.useCase.GetFairytaleUseCase;
import dalbit.domain.fairytale.Category;
import dalbit.domain.fairytale.Fairytale;
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

import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FairytaleJpaController.class)
@AutoConfigureRestDocs
class FairytaleJpaControllerDocsTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GetFairytaleUseCase getFairytaleUseCase;

    @Test
    @WithMockUser
    @DisplayName("동화 목록 조회 - 성공")
    void getFairytaleList_Success() throws Exception {
        Fairytale mockFairytale = Fairytale.builder()
            .id(1L)
            .title("달빛 공주의 모험")
            .category(Category.TRADITIONAL)
            .content("안녕하세요. 이게, 내용입니다. 껄껄")
            .build();

        given(getFairytaleUseCase.getFairytale()).willReturn(List.of(mockFairytale));

        mockMvc.perform(get("/api/v1/dalbit/fairytale/list")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("fairytale-get-list-success",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data[].id").description("동화 고유 아이디"),
                    fieldWithPath("data[].title").description("동화 제목"),
                    fieldWithPath("data[].category").description("동화 카테고리"),
                    fieldWithPath("data").description("동화 목록 데이터").optional()
                )
            ));
    }
}