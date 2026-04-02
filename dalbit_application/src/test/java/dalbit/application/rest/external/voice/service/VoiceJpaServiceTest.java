package dalbit.application.rest.external.voice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;

import dalbit.application.persistence.jpa.voice.port.DeleteVoicePort;
import dalbit.application.persistence.jpa.voice.port.LoadVoicePort;
import dalbit.application.persistence.jpa.voice.port.SaveVoicePort;
import dalbit.application.storage.port.GenerateUploadUrlPort;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.common.storage.Category;
import dalbit.domain.voice.Voice;
import dalbit.domain.voice.VoiceName;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("VoiceJpaService, 비즈니스 로직 테스트")
class VoiceJpaServiceTest {

    @InjectMocks
    private VoiceJpaService voiceJpaService;

    @Mock private SaveVoicePort saveVoicePort;
    @Mock private LoadVoicePort loadVoicePort;
    @Mock private DeleteVoicePort deleteVoicePort;
    @Mock private GenerateUploadUrlPort generateUploadUrlPort;

    @Mock private Voice voice;

    @Captor private ArgumentCaptor<Voice> voiceCaptor;
    @Captor private ArgumentCaptor<List<String>> pathsCaptor;

    private final Long USER_ID = 1L;
    private final String EXTERNAL_VOICE_ID = "voice-test-id";
    private final String VOICE_NAME = "voiceName";
    private final String VOICE_NEW_NAME = "newName";

    @Nested
    @DisplayName("목소리 등록 로직 테스트")
    class registerVoice {

        @Test
        @DisplayName("목소리 등록 완료: DB에 새로운 목소리 등록")
        void success_voice_register() {
            given(loadVoicePort.existsByUserIdAndName(USER_ID, VOICE_NAME)).willReturn(false);

            voiceJpaService.registerVoice(USER_ID, VOICE_NAME);

            then(saveVoicePort).should(times(1)).saveVoice(voiceCaptor.capture());

            Voice savedVoice = voiceCaptor.getValue();
            assertThat(savedVoice.getUserId()).isEqualTo(USER_ID);
            assertThat(savedVoice.getName().getValue()).isEqualTo(VOICE_NAME);
        }

        @Test
        @DisplayName("목소리 등록 실패: 이미 존재하는 목소리 이름")
        void fail_voice_register_existing_name() {
            given(loadVoicePort.existsByUserIdAndName(USER_ID, VOICE_NAME)).willReturn(true);

            assertThatThrownBy(() -> voiceJpaService.registerVoice(USER_ID, VOICE_NAME))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.ALREADY_EXIST_VOICE_NAME.getMessage());

            then(saveVoicePort).should(never()).saveVoice(any());
        }
    }

    @Nested
    @DisplayName("목소리 업로드 url 생성 로직 호출 테스트")
    class getVoiceUploadUrls {
        @Test
        @DisplayName("목소리 업로드 url 리스트 생성 완료")
        void success_generate_upload_urls() {
            Category mockCategory = mock(Category.class);
            String targetId = "target-123";
            int count = 3;
            List<String> expectedUrls = List.of("url1", "url2", "url3");

            given(mockCategory.generatePath(targetId, 1)).willReturn("path/1");
            given(mockCategory.generatePath(targetId, 2)).willReturn("path/2");
            given(mockCategory.generatePath(targetId, 3)).willReturn("path/3");

            given(generateUploadUrlPort.generateUploadUrls(any())).willReturn(expectedUrls);

            List<String> resultUrls = voiceJpaService.getVoiceUploadUrls(mockCategory, targetId, count);

            assertThat(resultUrls).isEqualTo(expectedUrls);

            then(generateUploadUrlPort).should(times(1)).generateUploadUrls(pathsCaptor.capture());
            List<String> capturedPaths = pathsCaptor.getValue();

            assertThat(capturedPaths).hasSize(3);
            assertThat(capturedPaths).containsExactly("path/1", "path/2", "path/3");
        }
    }

    @Nested
    @DisplayName("목소리 리스트 요청 로직 테스트")
    class getVoiceList {

        @Test
        @DisplayName("요청 성공: 목소리 리스트 반환")
        void success_get_voice_list() {
            List<Voice> expectedList = List.of(voice);
            given(loadVoicePort.loadAllVoicesByUserId(USER_ID)).willReturn(expectedList);

            List<Voice> result = voiceJpaService.getVoiceList(USER_ID);

            assertThat(result).isEqualTo(expectedList);
        }
    }

    @Nested
    @DisplayName("목소리 이름 업데이트 로직 테스트")
    class Describe_updateVoiceName {

        @Test
        @DisplayName("목소리 이름 업데이트 성공: DB에 업데이트")
        void success_update_voice_name() {
            given(loadVoicePort.existsByUserIdAndName(USER_ID, VOICE_NEW_NAME)).willReturn(false);
            given(loadVoicePort.loadVoiceByUserIdAndExternalId(USER_ID, EXTERNAL_VOICE_ID)).willReturn(Optional.of(voice));

            voiceJpaService.updateVoiceName(USER_ID, EXTERNAL_VOICE_ID, VOICE_NEW_NAME);

            then(voice).should(times(1)).updateVoiceName(any(VoiceName.class));
            then(saveVoicePort).should(times(1)).saveVoice(voice);
        }

        @Test
        @DisplayName("목소리 이름 업데이트 실패: 중복된 목소리 이름")
        void fail_when_new_name_already_exists() {
            given(loadVoicePort.existsByUserIdAndName(USER_ID, VOICE_NEW_NAME)).willReturn(true);

            assertThatThrownBy(() -> voiceJpaService.updateVoiceName(USER_ID, EXTERNAL_VOICE_ID, VOICE_NEW_NAME))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.ALREADY_EXIST_VOICE_NAME.getMessage());

            then(loadVoicePort).should(never()).loadVoiceByUserIdAndExternalId(any(), any());
            then(saveVoicePort).should(never()).saveVoice(any());
        }

        @Test
        @DisplayName("목소리 이름 업데이트 실패: 해당하는 목소리가 DB에 등록되어 있지 않음.")
        void fail_when_voice_not_found() {
            given(loadVoicePort.existsByUserIdAndName(USER_ID, VOICE_NEW_NAME)).willReturn(false);
            given(loadVoicePort.loadVoiceByUserIdAndExternalId(USER_ID, EXTERNAL_VOICE_ID)).willReturn(Optional.empty());

            assertThatThrownBy(() -> voiceJpaService.updateVoiceName(USER_ID, EXTERNAL_VOICE_ID, VOICE_NEW_NAME))
                .isInstanceOf(DalbitException.class)
                .hasMessageContaining(ErrorCode.NOT_EXIST_VOICE.getMessage());

            then(saveVoicePort).should(never()).saveVoice(any());
        }
    }

    @Nested
    @DisplayName("목소리 삭제 로직 테스트")
    class Describe_deleteVoice {

        @Test
        @DisplayName("목소리 삭제 완료: DB에서 해당하는 목소리 삭제")
        void success_delete_voice() {
            voiceJpaService.deleteVoice(USER_ID, EXTERNAL_VOICE_ID);

            then(deleteVoicePort).should(times(1)).deleteVoiceByUserIdAndExternalId(USER_ID, EXTERNAL_VOICE_ID);
        }
    }
}