package com.write.api.adapters.in.web.controller.auth;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerTest {

    private final String URL = "/v1/auth/";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Test
    void shouldCreateNewUser() {
        this.helper.createNewUser();
    }

    @Test
    void shouldReturn409BecauseUserNameAlreadyExists() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID();

        CreateUserDTO dto = new CreateUserDTO(
                user.dto().name(),
                "user" + key + "@gmail.com",
                "12345678"
        );

        MvcResult result = mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<AuthTokenResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("Username already exists");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key.toString());
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturn409BecauseUserEmailAlreadyExists() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID();

        CreateUserDTO dto = new CreateUserDTO(
                "user "+ key,
                user.dto().email(),
                "12345678"
        );

        MvcResult result = mockMvc.perform(post(URL + "/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<AuthTokenResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("email already exists");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key.toString());
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldSuccessMakeLogin() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID();

        LoginUserDTO dto = new LoginUserDTO(
                user.dto().email(),
                "12345678"
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<AuthTokenResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(key.toString());
        assertThat(response.data()).isNotNull();
        assertThat(response.data().token()).isNotBlank();
        assertThat(response.data().refreshToken()).isNotBlank();
        assertThat(response.data().user().getId()).isEqualTo(user.tokens().user().getId());

    }

    @Test
    void shouldFailTheMakeLoginBecausePasswordWrong() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID();

        LoginUserDTO dto = new LoginUserDTO(
                user.dto().email(),
                "12345678111"
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<AuthTokenResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(key.toString());
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailTheMakeLoginBecauseEmailWrong() throws Exception {
        var key = UUID.randomUUID();

        LoginUserDTO dto = new LoginUserDTO(
                "user"+key+"@gmail.com",
                "1234567811122"
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<AuthTokenResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(key.toString());
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldSuccessLogout() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID();

        MvcResult result = mockMvc.perform(get(URL + "/logout")
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                ).andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<AuthTokenResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("success");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key.toString());
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldFailLogoutBecauseTokenIsMissed() throws Exception {
        var key = UUID.randomUUID();

        mockMvc.perform(get(URL + "/logout")
                        .header("X-Idempotency-Key", key)
                ).andExpect(status().isUnauthorized())
                .andReturn();

    }

    // LOGOUT

    @Test
    void shouldSuccessTheMakeRefreshToken() throws Exception {
        UserTest user = this.helper.createNewUser();
        var key = UUID.randomUUID();

        MvcResult result = mockMvc.perform(get(URL + "/refresh-token/" + user.tokens().refreshToken())
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<AuthTokenResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(key.toString());
        assertThat(response.data()).isNotNull();
        assertThat(response.data().token()).isNotBlank();
        assertThat(response.data().refreshToken()).isNotBlank();
        assertThat(response.data().user().getId()).isEqualTo(user.tokens().user().getId());
    }

    @Test
    void shouldFailLogoutUserTokenMissed() throws Exception {
        UserTest user = this.helper.createNewUser();
        var key = UUID.randomUUID();

        mockMvc.perform(get(URL + "/refresh-token/" + user.tokens().refreshToken())
                        .header("X-Idempotency-Key", key)
                ).andExpect(status().isUnauthorized()).andReturn();
    }

}
