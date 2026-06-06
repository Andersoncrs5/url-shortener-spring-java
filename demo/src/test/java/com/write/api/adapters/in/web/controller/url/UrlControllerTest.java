package com.write.api.adapters.in.web.controller.url;

import com.fasterxml.jackson.core.type.TypeReference;
import com.write.api.adapters.in.web.controller.BaseControllerTest;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class UrlControllerTest extends BaseControllerTest {
    private final String URL = "/v1/url";

    @Test
    void shouldCreateUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        this.helper.createUrl(user, null);
    }

    @Test
    void shouldCreateUrlInternal() throws Exception {
        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();
        UserTest user = this.helper.createNewUser();
        String apiKey = this.helper.createApiKey(superAdm, user);

        var key = UUID.randomUUID().toString();

        CreateUrlDTO dto = new CreateUrlDTO(
                "https://example.com/article/" + idGen.nextId(),
                "My title",
                "Any desc",
                "https://example.com/favicon.ico",
                "example.com",
                UrlAccessTypeEnum.PUBLIC,
                null,
                LocalDateTime.now().plusDays(7)
        );

        mockMvc.perform(post(URL+ "/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("X-API-KEY", apiKey)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldFailCreateUrlInternalBecauseApiKeyIsMissed() throws Exception {

        var key = UUID.randomUUID().toString();

        CreateUrlDTO dto = new CreateUrlDTO(
                "https://example.com/article/" + idGen.nextId(),
                "My title",
                "Any desc",
                "https://example.com/favicon.ico",
                "example.com",
                UrlAccessTypeEnum.PUBLIC,
                null,
                LocalDateTime.now().plusDays(7)
        );

        mockMvc.perform(post(URL+ "/internal")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void shouldDeleteSoftUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/" + url.id())
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token()))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("deleted");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldFailTheDeleteByIdSoftUrlBecauseKeyIsMissed() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        mockMvc.perform(delete(URL + "/" + url.id())
                        .header("Authorization", "Bearer " + user.tokens().token()))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    void shouldFailDeleteSoftUrlBecauseNotFound() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/" + user.tokens().user().getId())
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token()))
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("not found");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNull();

    }

    @Test
    void shouldDeleteForceUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/" + url.id() + "/force")
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token()))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.message())
                .isNotBlank()
                .containsIgnoringCase("forced");

        assertThat(response.traceId())
                .isNotBlank()
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailDeleteForceUrlBecauseKeyIsMissed() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        mockMvc.perform(delete(URL + "/" + url.id() + "/force")
                        .header("Authorization", "Bearer " + user.tokens().token()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailDeleteForceUrlBecauseNotFound() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(
                        delete(URL + "/" + idGen.nextId() + "/force")
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                )
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.message())
                .isNotBlank()
                .containsIgnoringCase("not found");

        assertThat(response.traceId())
                .isNotBlank()
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailDeleteForceUrlBecauseIdIsInvalid() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID().toString();

        mockMvc.perform(
                        delete(URL + "/0/force")
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailDeleteForceUrlBecauseUserIsUnauthorized() throws Exception {
        var key = UUID.randomUUID().toString();

        mockMvc.perform(
                        delete(URL + "/" + idGen.nextId() + "/force")
                                .header("X-Idempotency-Key", key)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO created = this.helper.createUrl(user, null);

        var key = UUID.randomUUID().toString();

        UpdateUrlDTO dto = new UpdateUrlDTO(
                "https://updated-example.com",
                "Updated title",
                "Updated description",
                "https://updated-example.com/favicon.ico",
                "updated-example.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PRIVATE,
                "12345678",
                LocalDateTime.now().plusDays(30)
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + created.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isTrue();

        assertThat(response.message())
                .isNotBlank()
                .containsIgnoringCase("updated");

        assertThat(response.traceId())
                .isNotBlank()
                .isEqualTo(key);

        assertThat(response.data()).isNotNull();

        UrlResponseDTO data = response.data();

        assertThat(data.originalUrl()).isEqualTo(dto.originalUrl());
        assertThat(data.title()).isEqualTo(dto.title());
        assertThat(data.description()).isEqualTo(dto.description());
        assertThat(data.faviconUrl()).isEqualTo(dto.faviconUrl());
        assertThat(data.domain()).isEqualTo(dto.domain());
        assertThat(data.status()).isEqualTo(dto.status());
        assertThat(data.accessType()).isEqualTo(dto.accessType());
    }

    @Test
    void shouldFailUpdateUrlBecauseIdempotencyKeyIsMissed() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO created = this.helper.createUrl(user, null);

        UpdateUrlDTO dto = new UpdateUrlDTO(
                "https://updated-example.com",
                "Updated title",
                "Updated description",
                null,
                "updated-example.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PUBLIC,
                null,
                null
        );

        mockMvc.perform(
                        patch(URL + "/" + created.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailUpdateUrlBecauseUrlNotFound() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID().toString();

        UpdateUrlDTO dto = new UpdateUrlDTO(
                "https://updated-example.com",
                "Updated title",
                "Updated description",
                null,
                "updated-example.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PUBLIC,
                null,
                null
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + idGen.nextId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.message())
                .isNotBlank()
                .containsIgnoringCase("not found");

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailUpdateUrlBecauseIdIsInvalid() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID().toString();

        UpdateUrlDTO dto = new UpdateUrlDTO(
                "https://updated-example.com",
                "Updated title",
                "Updated description",
                null,
                "updated-example.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PUBLIC,
                null,
                null
        );

        mockMvc.perform(
                        patch(URL + "/0")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailUpdateUrlBecauseUserIsUnauthorized() throws Exception {
        var key = UUID.randomUUID().toString();

        UpdateUrlDTO dto = new UpdateUrlDTO(
                "https://updated-example.com",
                "Updated title",
                "Updated description",
                null,
                "updated-example.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PUBLIC,
                null,
                null
        );

        mockMvc.perform(
                        patch(URL + "/" + idGen.nextId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailUpdateUrlBecauseTitleIsTooLong() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO created = this.helper.createUrl(user, null);

        var key = UUID.randomUUID().toString();

        String longTitle = "a".repeat(300);

        UpdateUrlDTO dto = new UpdateUrlDTO(
                "https://updated-example.com",
                longTitle,
                "Updated description",
                null,
                "updated-example.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PUBLIC,
                null,
                null
        );

        mockMvc.perform(
                        patch(URL + "/" + created.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailUpdateUrlBecausePasswordIsTooShort() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO created = this.helper.createUrl(user, null);

        var key = UUID.randomUUID().toString();

        UpdateUrlDTO dto = new UpdateUrlDTO(
                "https://updated-example.com",
                "Updated title",
                "Updated description",
                null,
                "updated-example.com",
                UrlStatusEnum.ACTIVE,
                UrlAccessTypeEnum.PUBLIC,
                "123",
                null
        );

        mockMvc.perform(
                        patch(URL + "/" + created.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }
}
