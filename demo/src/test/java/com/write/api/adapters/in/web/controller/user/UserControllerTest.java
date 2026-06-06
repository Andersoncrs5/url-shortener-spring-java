package com.write.api.adapters.in.web.controller.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.write.api.adapters.in.web.controller.BaseControllerTest;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.application.dto.user.UserResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UserControllerTest extends BaseControllerTest {
    private final String URL = "/v1/user";

    @Test
    void shouldFailDeleteBecauseTokenIsMissed() throws Exception {
        var key = UUID.randomUUID();

        mockMvc.perform(delete(URL)
                        .header("X-Idempotency-Key", key)
                ).andExpect(status().isUnauthorized())
                .andReturn();

    }

    @Test
    void shouldSuccessTheDeleteUser() throws Exception {
        UserTest user = this.helper.createNewUser();
        var key = UUID.randomUUID();

        MvcResult result = mockMvc.perform(delete(URL)
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
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailTheDeleteUserBecauseIdempotencyKeyMissed() throws Exception {
        UserTest user = this.helper.createNewUser();

        mockMvc.perform(delete(URL)
                .header("Authorization", "Bearer " + user.tokens().token())
        ).andExpect(status().isBadRequest()).andReturn();
    }

    @Test
    void shouldFailTheDeleteUserBecauseTokenMissed() throws Exception {
        var key = UUID.randomUUID();

        mockMvc.perform(delete(URL)
                .header("X-Idempotency-Key", key)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    // UPDATE
    @Test
    void shouldSuccessTheUpdateUser() throws Exception {
        UserTest user = this.helper.createNewUser();
        var key = UUID.randomUUID().toString();

        UpdateUserDTO dto = new UpdateUserDTO(
                "userupdated" + key,
                "234345236456"
        );

        MvcResult result = mockMvc.perform(patch(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("X-Idempotency-Key", key)
                .header("Authorization", "Bearer " + user.tokens().token())
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UserResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isNotNull().isEqualTo(user.tokens().user().getId());
        assertThat(response.data().name()).isEqualTo(dto.name());
    }

    @Test
    void shouldFailTheUpdateUserBecauseTokenMissed() throws Exception {
        var key = UUID.randomUUID().toString();

        UpdateUserDTO dto = new UpdateUserDTO(
                "userupdated" + key,
                "234345236456"
        );

        mockMvc.perform(patch(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("X-Idempotency-Key", key)
        ).andExpect(status().isUnauthorized()).andReturn();
    }

    @Test
    void shouldFailTheUpdateUserBecauseIdempotencyKeyMissed() throws Exception {
        UserTest user = this.helper.createNewUser();
        var key = UUID.randomUUID().toString();

        UpdateUserDTO dto = new UpdateUserDTO(
                "userupdated" + key,
                "234345236456"
        );

        mockMvc.perform(patch(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + user.tokens().token())
        ).andExpect(status().isBadRequest()).andReturn();
    }



}
