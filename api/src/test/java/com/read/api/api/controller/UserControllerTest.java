package com.read.api.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.read.api.api.base.BaseIntegrationTest;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.user.UserDTO;
import com.read.api.api.utils.PageResponse;
import com.read.api.domain.model.UserModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UserControllerTest extends BaseIntegrationTest {
    private final String URL = "/v1/url-access-rule";

    @Test
    void shouldSearchUser() throws Exception {
        String token = this.createUser();
        var key = UUID.randomUUID().toString();

        this.createManyUsers(20);

        MvcResult result = mockMvc.perform(get(this.URL)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<PageResponse<UserDTO>> typeRef = new TypeReference<>() {};

        objectMapper.readValue(registerJson, typeRef);

    }

    @Test
    void shouldSearchUserById() throws Exception {
        String token = this.createUser();
        UserModel userFast = this.createUserFast();
        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(get(this.URL + "/" + userFast.getId())
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andDo(print())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHTTP<UserDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<UserDTO> http = objectMapper.readValue(registerJson, typeRef);

        assertThat(http.status()).isTrue();
        assertThat(http.version()).isEqualTo(1);
        assertThat(http.traceId()).isEqualTo(key);
        assertThat(http.data().getId()).isEqualTo(userFast.getId());
    }

    @Test
    void shouldReturnNotFoundSearchUserById() throws Exception {
        String token = this.createUser();
        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(get(this.URL + "/" + this.generator.nextId())
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHTTP<UserDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<UserDTO> http = objectMapper.readValue(registerJson, typeRef);

        assertThat(http.status()).isFalse();
        assertThat(http.version()).isEqualTo(1);
        assertThat(http.traceId()).isEqualTo(key);
        assertThat(http.data()).isNull();
    }

    @Test
    void shouldCheckIfEmailExists() throws Exception {
        String token = this.createUser();
        UserModel user = this.createUserFast();

        MvcResult result = mockMvc.perform(
                        get(this.URL + "/email-exists")
                                .param("email", user.getEmail())
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<Boolean>> typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.version()).isEqualTo(1);
        assertThat(response.data()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenEmailDoesNotExist() throws Exception {
        String token = this.createUser();

        MvcResult result = mockMvc.perform(
                        get(this.URL + "/email-exists")
                                .param("email", "not-found@gmail.com")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<Boolean>> typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.version()).isEqualTo(1);
        assertThat(response.data()).isFalse();
    }

    @Test
    void shouldCheckIfNameExists() throws Exception {
        String token = this.createUser();
        UserModel user = this.createUserFast();

        MvcResult result = mockMvc.perform(
                        get(this.URL + "/name-exists")
                                .param("name", user.getName())
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<Boolean>> typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.version()).isEqualTo(1);
        assertThat(response.data()).isTrue();
        assertThat(response.message()).isEqualTo("Name checked");
    }

    @Test
    void shouldReturnFalseWhenNameDoesNotExist() throws Exception {
        String token = this.createUser();

        MvcResult result = mockMvc.perform(
                        get(this.URL + "/name-exists")
                                .param("name", "user-not-found")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<Boolean>> typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.version()).isEqualTo(1);
        assertThat(response.data()).isFalse();
        assertThat(response.message()).isEqualTo("Name checked");
    }
}
