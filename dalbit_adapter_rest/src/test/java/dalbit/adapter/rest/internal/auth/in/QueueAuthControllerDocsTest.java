package dalbit.adapter.rest.internal.auth.in;

import dalbit.application.rest.internal.auth.useCase.AuthenticateQueueUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QueueAuthController.class)
@AutoConfigureRestDocs
class QueueAuthControllerDocsTest {

    @Autowired private MockMvc mockMvc;

    @MockitoBean private AuthenticateQueueUseCase authenticateQueueUseCase;

    @Test
    @WithMockUser
    @DisplayName("RabbitMQ 유저 인증 (Connection) - 허용")
    void authenticateUser_Allow() throws Exception {
        given(authenticateQueueUseCase.authenticateConnection(anyString(), anyString())).willReturn(true);

        mockMvc.perform(post("/internal/queue/auth/user")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "usr_UUID-1234")
                .param("password", "eyJh...JWT토큰..."))
            .andExpect(status().isOk())
            .andExpect(content().string("allow"))
            .andDo(document("queue-auth-user-allow",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                formParameters(
                    parameterWithName("username").description("RabbitMQ 접속 유저명 (예: usr_{externalId} 또는 dev_{serialNumber})"),
                    parameterWithName("password").description("비밀번호 (유저: JWT 토큰, 디바이스: Secret Key)"),
                    parameterWithName("_csrf").ignored()
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("RabbitMQ Vhost 인가 - 무조건 허용")
    void authorizeVhost_Allow() throws Exception {
        mockMvc.perform(post("/internal/queue/auth/vhost")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("vhost", "/")
                .param("username", "usr_UUID-1234")
                .param("ip", "192.168.0.1"))
            .andExpect(status().isOk())
            .andExpect(content().string("allow"))
            .andDo(document("queue-auth-vhost-allow",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                formParameters(
                    parameterWithName("vhost").description("접근하려는 Virtual Host 명"),
                    parameterWithName("username").description("접속 유저명").optional(),
                    parameterWithName("ip").description("접속 IP").optional(),
                    parameterWithName("_csrf").ignored()
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("RabbitMQ 리소스(Exchange/Queue) 인가 - 허용")
    void authorizeResource_Allow() throws Exception {
        given(authenticateQueueUseCase.isResourceAuthorized(anyString(), anyString(), anyString(), anyString())).willReturn(true);

        mockMvc.perform(post("/internal/queue/auth/resource")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "usr_UUID-1234")
                .param("resource", "exchange")
                .param("name", "amq.topic")
                .param("permission", "write"))
            .andExpect(status().isOk())
            .andExpect(content().string("allow"))
            .andDo(document("queue-auth-resource-allow",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                formParameters(
                    parameterWithName("username").description("접속 유저명"),
                    parameterWithName("resource").description("리소스 타입 (exchange, queue)"),
                    parameterWithName("name").description("익스체인지 또는 큐의 이름"),
                    parameterWithName("permission").description("요청 권한 (read, write, configure)"),
                    parameterWithName("_csrf").ignored()
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("RabbitMQ 토픽(Routing Key) 인가 - 허용")
    void authorizeTopic_Allow() throws Exception {
        given(authenticateQueueUseCase.isTopicAuthorized(anyString(), anyString(), anyString())).willReturn(true);

        mockMvc.perform(post("/internal/queue/auth/topic")
                .with(csrf())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .param("username", "usr_UUID-1234")
                .param("routing_key", "dalbit/topic/SN12345")
                .param("permission", "write"))
            .andExpect(status().isOk())
            .andExpect(content().string("allow"))
            .andDo(document("queue-auth-topic-allow",
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()),
                formParameters(
                    parameterWithName("username").description("접속 유저명"),
                    parameterWithName("routing_key").description("메시지 라우팅 키 (토픽 명)"),
                    parameterWithName("permission").description("요청 권한 (read, write)"),
                    parameterWithName("_csrf").ignored()
                )
            ));
    }
}
