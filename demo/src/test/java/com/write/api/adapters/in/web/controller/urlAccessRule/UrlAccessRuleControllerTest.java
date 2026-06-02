package com.write.api.adapters.in.web.controller.urlAccessRule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.application.dto.urlAccessRule.UrlAccessRuleResponseDTO;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlAccessRuleControllerTest {
    private final String URL = "/v1/url-access-rule";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;
    @Autowired private SnowflakeIdGenerator idGen;

    @Test
    void shouldCreateNewAccessRule() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        UrlAccessRuleResponseDTO urlAccessRuleResponseDTO = this.helper.addAccessRuleToUrl(url, user);
    }

    @Test
    void shouldDeleteAccessRule() throws Exception {
        var key = UUID.randomUUID().toString();

        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        UrlAccessRuleResponseDTO urlAccessRuleDTO = this.helper.addAccessRuleToUrl(url, user);

        MvcResult result = mockMvc.perform(delete(URL + "/" + urlAccessRuleDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
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
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNull();

    }

    @Test
    void shouldFailTheDeleteAccessRuleBecauseUrlNotFound() throws Exception {
        var key = UUID.randomUUID().toString();

        UserTest user = this.helper.createNewUser();

        MvcResult result = mockMvc.perform(delete(URL + "/" + idGen.nextId())
                        .contentType(MediaType.APPLICATION_JSON)
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
        assertThat(response.message()).containsIgnoringCase("not found");
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNull();

    }

    @Test
    void shouldUpdateAccessRule() throws Exception {
        var key = UUID.randomUUID().toString();

        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlAccessRuleResponseDTO saved = this.helper.addAccessRuleToUrl(url, user);

        UpdateUrlAccessRuleDTO dto = new UpdateUrlAccessRuleDTO(
                UrlAccessRuleTypeEnum.COUNTRY_ALLOW,
                "US",
                true,
                LocalDateTime.now().plusDays(2)
        );

        MvcResult result = mockMvc.perform(patch(URL + "/" + saved.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlAccessRuleResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlAccessRuleResponseDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.message()).isEqualTo("Url Access Rule updated");

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isEqualTo(saved.id());
        assertThat(response.data().urlId()).isEqualTo(url.id());
        assertThat(response.data().type()).isEqualTo(UrlAccessRuleTypeEnum.COUNTRY_ALLOW);
        assertThat(response.data().ruleValue()).isEqualTo("US");
        assertThat(response.data().active()).isTrue();
    }

    @Test
    void shouldFailTheUpdateAccessRuleBecauseRuleNotFound() throws Exception {
        var key = UUID.randomUUID().toString();

        UserTest user = this.helper.createNewUser();

        UpdateUrlAccessRuleDTO dto = new UpdateUrlAccessRuleDTO(
                UrlAccessRuleTypeEnum.COUNTRY_ALLOW,
                "US",
                true,
                LocalDateTime.now().plusDays(2)
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
        assertThat(response.message()).containsIgnoringCase("not found");
        assertThat(response.traceId()).isEqualTo(key);
        assertThat(response.data()).isNull();
    }

}
