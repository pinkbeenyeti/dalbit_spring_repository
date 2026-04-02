package dalbit.application.rest.internal.auth.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import dalbit.application.auth.jwt.port.VerifyTokenPort;
import dalbit.application.persistence.jpa.device.port.LoadDevicePort;
import dalbit.application.persistence.jpa.user.port.LoadUserPort;
import dalbit.domain.device.Device;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
@DisplayName("QueueAuthService, 비즈니스 로직 테스트")
class QueueAuthServiceTest {

    @InjectMocks
    private QueueAuthService queueAuthService;

    @Mock private VerifyTokenPort verifyTokenPort;
    @Mock private LoadDevicePort loadDevicePort;
    @Mock private LoadUserPort loadUserPort;
    @Mock private Device mockDevice;

    private static final String USER_PREFIX = "usr_";
    private static final String DEVICE_PREFIX = "dev_";
    private static final String EXTERNAL_USER_ID = "test-user-id";
    private static final String SERIAL_NUMBER = "SN12345";
    private static final String VALID_TOKEN = "valid-jwt-token";
    private static final String VALID_TOPIC = "device/status/" + SERIAL_NUMBER;
    private static final Long USER_ID = 1L;

    @Nested
    @DisplayName("큐 연결 권한 확인 테스트")
    class authenticateConnection {

        @Test
        @DisplayName("권한 거부: 이름, 비밀번호 누락")
        void fail_input_null() {
            assertThat(queueAuthService.authenticateConnection(null, "password")).isFalse();
            assertThat(queueAuthService.authenticateConnection("userName", null)).isFalse();
        }

        @Nested
        @DisplayName("클라이언트 요청")
        class user_request {

            private final String userName = USER_PREFIX + EXTERNAL_USER_ID;

            @Test
            @DisplayName("연결 허용: 토큰 추출 ID, 요청 ID 동일")
            void success_token_and_userId_valid() {
                given(verifyTokenPort.validateToken(VALID_TOKEN)).willReturn(true);
                given(verifyTokenPort.getExternalIdFromToken(VALID_TOKEN)).willReturn(EXTERNAL_USER_ID);

                boolean result = queueAuthService.authenticateConnection(userName, VALID_TOKEN);

                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("연결 거부: 유효하지 않은 토큰")
            void fail_token_invalid() {
                given(verifyTokenPort.validateToken("invalid_token")).willReturn(false);

                boolean result = queueAuthService.authenticateConnection(userName, "invalid_token");

                assertThat(result).isFalse();
                then(verifyTokenPort).should(never()).getExternalIdFromToken(anyString());
            }

            @Test
            @DisplayName("토큰은 유효하지만 토큰의 ID와 요청 ID가 다르면 false를 반환한다 (토큰 탈취 방지)")
            void fail_userId_invalid() {
                given(verifyTokenPort.validateToken(VALID_TOKEN)).willReturn(true);
                given(verifyTokenPort.getExternalIdFromToken(VALID_TOKEN)).willReturn("different-ext-id");

                boolean result = queueAuthService.authenticateConnection(userName, VALID_TOKEN);

                assertThat(result).isFalse();
            }
        }
    }

    @Nested
    @DisplayName("토픽 연결 권한 확인 테스트")
    class isAuthorized {

        private final String PERMISSION = "read";

        @Test
        @DisplayName("연결 허용: 토픽 외 요청, resource Type이 'topic'이 아님.")
        void success_not_topic() {
            boolean result = queueAuthService.isAuthorized("anyUser", "exchange", VALID_TOPIC, PERMISSION);
            assertThat(result).isTrue();
        }

        @Nested
        @DisplayName("클라이언트 요청")
        class user_request {

            private final String userName = USER_PREFIX + EXTERNAL_USER_ID;

            @Test
            @DisplayName("연결 허용: 모든 조건 만족")
            void success_all_conditions_met() {
                given(loadUserPort.loadUserIdByExternalId(EXTERNAL_USER_ID)).willReturn(Optional.of(USER_ID));
                given(loadDevicePort.loadDeviceBySerialNumber(SERIAL_NUMBER)).willReturn(Optional.of(mockDevice));
                given(mockDevice.getUserId()).willReturn(USER_ID);

                boolean result = queueAuthService.isAuthorized(userName, "topic", VALID_TOPIC, PERMISSION);

                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("연결 거부: 토픽에서 시리얼 번호 탐색 불가")
            void fail_topic_pattern_mismatch() {
                boolean result = queueAuthService.isAuthorized(userName, "topic", "invalid/topic/format", PERMISSION);
                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("연결 거부: 존재하지 않는 유저")
            void fail_when_user_not_found() {
                given(loadUserPort.loadUserIdByExternalId(EXTERNAL_USER_ID)).willReturn(Optional.empty());
                given(loadDevicePort.loadDeviceBySerialNumber(SERIAL_NUMBER)).willReturn(Optional.of(mockDevice));

                boolean result = queueAuthService.isAuthorized(userName, "topic", VALID_TOPIC, PERMISSION);

                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("연결 거부: 존재하지 않는 시리얼 넘버(디바이스)")
            void fail_when_device_not_found() {
                given(loadUserPort.loadUserIdByExternalId(EXTERNAL_USER_ID)).willReturn(Optional.of(USER_ID));
                given(loadDevicePort.loadDeviceBySerialNumber(SERIAL_NUMBER)).willReturn(Optional.empty());

                boolean result = queueAuthService.isAuthorized(userName, "topic", VALID_TOPIC, PERMISSION);

                assertThat(result).isFalse();
            }

            @Test
            @DisplayName("연결 거부: 디바이스 소유 권한 없음")
            void fail_when_not_device_owner() {
                given(loadUserPort.loadUserIdByExternalId(EXTERNAL_USER_ID)).willReturn(Optional.of(USER_ID));
                given(loadDevicePort.loadDeviceBySerialNumber(SERIAL_NUMBER)).willReturn(Optional.of(mockDevice));
                given(mockDevice.getUserId()).willReturn(999L);

                boolean result = queueAuthService.isAuthorized(userName, "topic", VALID_TOPIC, PERMISSION);

                assertThat(result).isFalse();
            }
        }

        @Nested
        @DisplayName("디바이스 요청")
        class device_request {

            private final String userName = DEVICE_PREFIX + SERIAL_NUMBER;

            @Test
            @DisplayName("연결 허용: 토픽에 요청한 시리얼 넘버 확인")
            void success_topic_contains_serial() {
                boolean result = queueAuthService.isAuthorized(userName, "topic", VALID_TOPIC, PERMISSION);
                assertThat(result).isTrue();
            }

            @Test
            @DisplayName("연결 거부: 토픽에 요청한 시리얼 넘버 확인 불가")
            void fail_topic_contains_serial() {
                boolean result = queueAuthService.isAuthorized(userName, "topic", "device/status/SN1234", PERMISSION);
                assertThat(result).isFalse();
            }
        }
    }

}