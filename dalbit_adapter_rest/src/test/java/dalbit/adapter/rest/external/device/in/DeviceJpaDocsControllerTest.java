package dalbit.adapter.rest.external.device.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import dalbit.adapter.rest.external.device.dto.request.RegisterDeviceOwnerRequest;
import dalbit.adapter.rest.external.device.dto.request.UpdateDeviceNameRequest;
import dalbit.application.rest.external.device.useCase.GetDevicesUseCase;
import dalbit.application.rest.external.device.useCase.RegisterDeviceUseCase;
import dalbit.application.rest.external.device.useCase.UpdateDeviceUseCase;
import dalbit.domain.common.error.DalbitException;
import dalbit.domain.common.error.ErrorCode;
import dalbit.domain.device.Device;
import dalbit.domain.device.DeviceName;
import dalbit.domain.device.DeviceType;
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

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(DeviceJpaController.class)
@AutoConfigureRestDocs
class DeviceJpaDocsControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    @MockitoBean private GetDevicesUseCase getDevicesUseCase;
    @MockitoBean private RegisterDeviceUseCase registerDeviceUseCase;
    @MockitoBean private UpdateDeviceUseCase updateDeviceUseCase;


    @Test
    @WithMockUser
    @DisplayName("내 기기 목록 조회 - 성공")
    void getMyDevices_Success() throws Exception {
        Device mockDevice = Device.builder()
            .id(1L)
            .userId(1L)
            .serialNumber("DAL-12345-BIT")
            .deviceSecret("1234")
            .type(DeviceType.MOON_LAMP)
            .name(DeviceName.of("거실 달빛등"))
            .build();

        given(getDevicesUseCase.getAllDevices(any())).willReturn(List.of(mockDevice));

        mockMvc.perform(get("/api/v1/dalbit/device/myDevices")
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andDo(document("device-get-my-devices-success",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지"),
                    fieldWithPath("data[].serialNumber").description("기기 시리얼 번호"),
                    fieldWithPath("data[].deviceType").description("기기 타입"),
                    fieldWithPath("data[].name").description("기기 이름")
                )
            ));
    }


    @Test
    @WithMockUser
    @DisplayName("기기 소유자 등록 - 성공")
    void registerDeviceOwner_Success() throws Exception {
        RegisterDeviceOwnerRequest request = new RegisterDeviceOwnerRequest("DAL-12345-BIT");

        mockMvc.perform(post("/api/v1/dalbit/device/register/owner")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("device-register-owner-success",
                preprocessRequest(prettyPrint()),
                requestFields(
                    fieldWithPath("serialNumber").description("등록할 기기의 시리얼 번호")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("기기 소유자 등록 - 실패 (이미 등록된 기기)")
    void registerDeviceOwner_Fail_AlreadyRegistered() throws Exception {
        RegisterDeviceOwnerRequest request = new RegisterDeviceOwnerRequest("DAL-DUPLICATE");

        doThrow(new DalbitException(ErrorCode.ALREADY_OWNED_DEVICE))
            .when(registerDeviceUseCase).registerDeviceOwner(anyString(), any());

        mockMvc.perform(post("/api/v1/dalbit/device/register/owner")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isConflict())
            .andDo(document("device-register-owner-fail",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("이미 등록된 기기입니다.")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("기기 이름 수정 - 성공")
    void updateDeviceName_Success() throws Exception {
        UpdateDeviceNameRequest request = new UpdateDeviceNameRequest("DAL-12345-BIT", "침실 무드등");

        mockMvc.perform(patch("/api/v1/dalbit/device/update/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andDo(document("device-update-name-success",
                preprocessRequest(prettyPrint()),
                requestFields(
                    fieldWithPath("serialNumber").description("기기 시리얼 번호"),
                    fieldWithPath("newName").description("새로 변경할 기기 이름")
                ),
                responseFields(
                    fieldWithPath("code").description("응답 코드"),
                    fieldWithPath("message").description("응답 메시지")
                )
            ));
    }

    @Test
    @WithMockUser
    @DisplayName("기기 이름 수정 - 실패 (권한 없음)")
    void updateDeviceName_Fail_Forbidden() throws Exception {
        UpdateDeviceNameRequest request = new UpdateDeviceNameRequest("NOT-MY-DEVICE-SERIAL", "name");

        doThrow(new DalbitException(ErrorCode.NOT_DEVICE_OWNER))
            .when(updateDeviceUseCase).updateDeviceName(any(), anyString(), any());

        mockMvc.perform(patch("/api/v1/dalbit/device/update/name")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isForbidden())
            .andDo(document("device-update-name-fail-forbidden",
                preprocessResponse(prettyPrint()),
                responseFields(
                    fieldWithPath("code").description("에러 코드"),
                    fieldWithPath("message").description("기기 수정 권한이 없습니다.")
                )
            ));
    }
}