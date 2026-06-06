package com.write.api.adapters.in.web.controller.urlRedirectRule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.write.api.adapters.in.web.controller.BaseControllerTest;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UpdateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.write.api.application.shared.validation.ValidationErrorResponse;
import com.write.api.core.domain.enums.BrowserEnum;
import com.write.api.core.domain.enums.ContinentEnum;
import com.write.api.core.domain.enums.MatchTypeEnum;
import com.write.api.core.domain.enums.OperatingSystemEnum;
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
public class UrlRedirectRuleControllerTest extends BaseControllerTest {
    private final String URL = "/v1/url-redirect-rule";

    @Test
    void shouldCreateRule() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        this.helper.createUrlRedirectRule(user, url);
    }

    @Test
    void shouldFailBecauseUrlNotFoundTheCreateRule() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID().toString();

        CreateUrlRedirectRuleDTO dto = new CreateUrlRedirectRuleDTO(
                idGen.nextId(),
                "BR",
                "PI",
                ContinentEnum.SOUTH_AMERICA,
                OperatingSystemEnum.ANDROID,
                BrowserEnum.CHROME,
                MatchTypeEnum.EXACT,
                "https://m.example.com/mobile",
                1,
                true,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};
        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank().containsIgnoringCase("url not found");
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseRuleAlreadyExistsTheCreateRule() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlRedirectRuleDTO rule = this.helper.createUrlRedirectRule(user, url);

        var key = UUID.randomUUID().toString();

        CreateUrlRedirectRuleDTO dto = new CreateUrlRedirectRuleDTO(
                rule.urlId(),
                rule.countryCode(),
                rule.region(),
                rule.continent(),
                rule.os(),
                rule.browser(),
                rule.matchType(),
                rule.redirectUrl(),
                rule.priority(),
                rule.active(),
                rule.startAt(),
                rule.endAt()
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};
        ResponseHttp<Void> response = objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank().containsIgnoringCase("rule already present");
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseIdempotencyKeyIsMissing() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        CreateUrlRedirectRuleDTO dto = new CreateUrlRedirectRuleDTO(
                url.id(),
                "BR",
                "PI",
                ContinentEnum.SOUTH_AMERICA,
                OperatingSystemEnum.ANDROID,
                BrowserEnum.CHROME,
                MatchTypeEnum.EXACT,
                "https://m.example.com/mobile",
                1,
                true,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailBecauseUserIsUnauthorized() throws Exception {
        CreateUrlRedirectRuleDTO dto = new CreateUrlRedirectRuleDTO(
                idGen.nextId(),
                "BR",
                "PI",
                ContinentEnum.SOUTH_AMERICA,
                OperatingSystemEnum.ANDROID,
                BrowserEnum.CHROME,
                MatchTypeEnum.EXACT,
                "https://m.example.com/mobile",
                1,
                true,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailBecauseUrlIdIsInvalid() throws Exception {
        UserTest user = this.helper.createNewUser();

        CreateUrlRedirectRuleDTO dto = new CreateUrlRedirectRuleDTO(
                0L,
                "BR",
                "PI",
                ContinentEnum.SOUTH_AMERICA,
                OperatingSystemEnum.ANDROID,
                BrowserEnum.CHROME,
                MatchTypeEnum.EXACT,
                "https://m.example.com/mobile",
                1,
                true,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<ValidationErrorResponse>> typeRef = new TypeReference<>() {};
        ResponseHttp<ValidationErrorResponse> response = objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.data()).isNotNull();
    }

    @Test
    void shouldFailBecauseRedirectUrlIsTooLong() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        String longUrl = "https://example.com/" + "a".repeat(2050);

        CreateUrlRedirectRuleDTO dto = new CreateUrlRedirectRuleDTO(
                url.id(),
                "BR",
                "PI",
                ContinentEnum.SOUTH_AMERICA,
                OperatingSystemEnum.ANDROID,
                BrowserEnum.CHROME,
                MatchTypeEnum.EXACT,
                longUrl,
                1,
                true,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Object>> typeRef = new TypeReference<>() {};
        ResponseHttp<Object> response = objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.data()).isNotNull();
    }

    @Test
    void shouldFailBecauseCountryCodeTooLong() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        CreateUrlRedirectRuleDTO dto = new CreateUrlRedirectRuleDTO(
                url.id(),
                "BRA",
                "PI",
                ContinentEnum.SOUTH_AMERICA,
                OperatingSystemEnum.ANDROID,
                BrowserEnum.CHROME,
                MatchTypeEnum.EXACT,
                "https://m.example.com/mobile",
                1,
                true,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<ValidationErrorResponse>> typeRef = new TypeReference<>() {};
        ResponseHttp<ValidationErrorResponse> response = objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.data()).isNotNull();
    }

    @Test
    void shouldDeleteRuleSuccessfully() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        UrlRedirectRuleDTO rule =
                this.helper.createUrlRedirectRule(user, url);

        String key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/" + rule.id())
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isEqualTo("Rule deleted");
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseRuleNotFoundOnDelete() throws Exception {
        UserTest user = this.helper.createNewUser();

        String key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/" + idGen.nextId())
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message())
                .containsIgnoringCase("rule not found");
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseUserIsUnauthorizedOnDelete() throws Exception {
        MvcResult result = mockMvc.perform(delete(URL + "/" + idGen.nextId())
                        .header("X-Idempotency-Key", UUID.randomUUID().toString()))
                .andExpect(status().isUnauthorized())
                .andReturn();

        assertThat(result).isNotNull();
    }

    @Test
    void shouldFailBecauseIdempotencyKeyIsMissingOnDelete() throws Exception {
        UserTest user = this.helper.createNewUser();

        mockMvc.perform(delete(URL + "/" + idGen.nextId())
                        .header("Authorization", "Bearer " + user.tokens().token()))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailBecauseIdIsInvalidOnDelete() throws Exception {
        UserTest user = this.helper.createNewUser();

        String key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/0")
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).isNotBlank();
    }

    @Test
    void shouldUpdateRuleSuccessfully() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlRedirectRuleDTO rule = this.helper.createUrlRedirectRule(user, url);

        String key = UUID.randomUUID().toString();

        UpdateUrlRedirectRuleDTO dto = new UpdateUrlRedirectRuleDTO(
                "US",
                "NY",
                ContinentEnum.NORTH_AMERICA,
                OperatingSystemEnum.IOS,
                BrowserEnum.SAFARI,
                MatchTypeEnum.PARTIAL,
                "https://m.example.com/us",
                10,
                false,
                rule.startAt(),
                rule.endAt()
        );

        MvcResult result = mockMvc.perform(patch(URL + "/" + rule.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlRedirectRuleDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlRedirectRuleDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.message()).isEqualTo("Rule updated");
        assertThat(response.traceId()).isEqualTo(key);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isEqualTo(rule.id());
        assertThat(response.data().urlId()).isEqualTo(rule.urlId());
        assertThat(response.data().countryCode()).isEqualTo(dto.countryCode());
        assertThat(response.data().region()).isEqualTo(dto.region());
        assertThat(response.data().continent()).isEqualTo(dto.continent());
        assertThat(response.data().os()).isEqualTo(dto.os());
        assertThat(response.data().browser()).isEqualTo(dto.browser());
        assertThat(response.data().matchType()).isEqualTo(dto.matchType());
        assertThat(response.data().redirectUrl()).isEqualTo(dto.redirectUrl());
        assertThat(response.data().priority()).isEqualTo(dto.priority());
        assertThat(response.data().active()).isEqualTo(dto.active());
    }

    @Test
    void shouldFailBecauseRuleNotFoundOnUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();

        String key = UUID.randomUUID().toString();

        UpdateUrlRedirectRuleDTO dto = new UpdateUrlRedirectRuleDTO(
                "US",
                "NY",
                ContinentEnum.NORTH_AMERICA,
                OperatingSystemEnum.IOS,
                BrowserEnum.SAFARI,
                MatchTypeEnum.PARTIAL,
                "https://m.example.com/us",
                10,
                false,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        MvcResult result = mockMvc.perform(patch(URL + "/" + idGen.nextId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message()).containsIgnoringCase("rule not found");
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseRuleAlreadyExistsOnUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        UrlRedirectRuleDTO rule1 =
                this.helper.createUrlRedirectRule(user, url);

        String secondRedirect =
                "https://m.example.com/other";

        CreateUrlRedirectRuleDTO createSecond =
                new CreateUrlRedirectRuleDTO(
                        url.id(),
                        "US",
                        "NY",
                        ContinentEnum.NORTH_AMERICA,
                        OperatingSystemEnum.IOS,
                        BrowserEnum.SAFARI,
                        MatchTypeEnum.PARTIAL,
                        secondRedirect,
                        10,
                        true,
                        LocalDateTime.now().plusHours(1),
                        LocalDateTime.now().plusDays(30)
                );

        String createKey = UUID.randomUUID().toString();

        MvcResult createResult = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", createKey)
                        .content(objectMapper.writeValueAsString(createSecond)))
                .andExpect(status().isCreated())
                .andReturn();

        String createJson =
                createResult.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlRedirectRuleDTO>> createType =
                new TypeReference<>() {};

        ResponseHttp<UrlRedirectRuleDTO> createdResponse =
                objectMapper.readValue(createJson, createType);

        UrlRedirectRuleDTO rule2 = createdResponse.data();

        String key = UUID.randomUUID().toString();

        UpdateUrlRedirectRuleDTO dto =
                new UpdateUrlRedirectRuleDTO(
                        rule1.countryCode(),
                        rule1.region(),
                        rule1.continent(),
                        rule1.os(),
                        rule1.browser(),
                        rule1.matchType(),
                        rule1.redirectUrl(),
                        rule1.priority(),
                        rule1.active(),
                        rule1.startAt(),
                        rule1.endAt()
                );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + rule2.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .header("X-Idempotency-Key", key)
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isConflict())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.message())
                .containsIgnoringCase("rule already present");
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseIdempotencyKeyIsMissingOnUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlRedirectRuleDTO rule = this.helper.createUrlRedirectRule(user, url);

        UpdateUrlRedirectRuleDTO dto = new UpdateUrlRedirectRuleDTO(
                "US",
                "NY",
                ContinentEnum.NORTH_AMERICA,
                OperatingSystemEnum.IOS,
                BrowserEnum.SAFARI,
                MatchTypeEnum.PARTIAL,
                "https://m.example.com/us",
                10,
                false,
                rule.startAt(),
                rule.endAt()
        );

        mockMvc.perform(patch(URL + "/" + rule.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailBecauseUserIsUnauthorizedOnUpdate() throws Exception {
        UpdateUrlRedirectRuleDTO dto = new UpdateUrlRedirectRuleDTO(
                "US",
                "NY",
                ContinentEnum.NORTH_AMERICA,
                OperatingSystemEnum.IOS,
                BrowserEnum.SAFARI,
                MatchTypeEnum.PARTIAL,
                "https://m.example.com/us",
                10,
                false,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        mockMvc.perform(patch(URL + "/" + idGen.nextId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailBecauseIdIsInvalidOnUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();

        UpdateUrlRedirectRuleDTO dto = new UpdateUrlRedirectRuleDTO(
                "US",
                "NY",
                ContinentEnum.NORTH_AMERICA,
                OperatingSystemEnum.IOS,
                BrowserEnum.SAFARI,
                MatchTypeEnum.PARTIAL,
                "https://m.example.com/us",
                10,
                false,
                LocalDateTime.now().plusHours(1),
                LocalDateTime.now().plusDays(30)
        );

        MvcResult result = mockMvc.perform(patch(URL + "/0")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<ValidationErrorResponse>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ValidationErrorResponse> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldFailBecauseRedirectUrlIsTooLongOnUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlRedirectRuleDTO rule = this.helper.createUrlRedirectRule(user, url);

        String longUrl = "https://example.com/" + "a".repeat(2050);

        UpdateUrlRedirectRuleDTO dto = new UpdateUrlRedirectRuleDTO(
                "US",
                "NY",
                ContinentEnum.NORTH_AMERICA,
                OperatingSystemEnum.IOS,
                BrowserEnum.SAFARI,
                MatchTypeEnum.PARTIAL,
                longUrl,
                10,
                false,
                rule.startAt(),
                rule.endAt()
        );

        MvcResult result = mockMvc.perform(patch(URL + "/" + rule.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<ValidationErrorResponse>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ValidationErrorResponse> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.data()).isNotNull();
    }

    @Test
    void shouldFailBecauseCountryCodeTooLongOnUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlRedirectRuleDTO rule = this.helper.createUrlRedirectRule(user, url);

        UpdateUrlRedirectRuleDTO dto = new UpdateUrlRedirectRuleDTO(
                "USA",
                "NY",
                ContinentEnum.NORTH_AMERICA,
                OperatingSystemEnum.IOS,
                BrowserEnum.SAFARI,
                MatchTypeEnum.PARTIAL,
                "https://m.example.com/us",
                10,
                false,
                rule.startAt(),
                rule.endAt()
        );

        MvcResult result = mockMvc.perform(patch(URL + "/" + rule.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<ValidationErrorResponse>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ValidationErrorResponse> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.data()).isNotNull();
    }

}