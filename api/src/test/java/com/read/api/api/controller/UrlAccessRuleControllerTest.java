package com.read.api.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.read.api.api.base.BaseIntegrationTest;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlAccessRule.UrlAccessRuleDTO;
import com.read.api.api.utils.PageResponse;
import com.read.api.domain.enums.UrlAccessRuleTypeEnum;
import com.read.api.domain.model.UrlAccessRuleModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UrlAccessRuleControllerTest extends BaseIntegrationTest {
    private final String URL = "/v1/url-access-rule";

    @Test
    void shouldFindAllUrlAccessRule() throws Exception {
        String token = this.createUser();
        ArrayList<UrlAccessRuleModel> rules = this.createManyUrlAccessRule(10);

        MvcResult result = mockMvc.perform(get(this.URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<PageResponse<UrlAccessRuleDTO>> typeRef = new TypeReference<>() {};

        objectMapper.readValue(registerJson, typeRef);
    }

    @Test
    void shouldReturnUrlAccessRuleWhenFindById() throws Exception {
        String token = this.createUser();
        UrlAccessRuleModel rule = this.createUrlAccessRule();

        MvcResult result = mockMvc.perform(get(this.URL + "/" + rule.getId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHTTP<UrlAccessRuleDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<UrlAccessRuleDTO> http = objectMapper.readValue(registerJson, typeRef);

        assertThat(http.status()).isTrue();

        assertThat(http.data()).isNotNull();
        assertThat(http.data().getId()).isEqualTo(rule.getId());
    }

    @Test
    void shouldReturnNotFoundWhenFindById() throws Exception {
        String token = this.createUser();

        MvcResult result = mockMvc.perform(get(this.URL + "/" + generator.nextId())
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHTTP<UrlAccessRuleDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<UrlAccessRuleDTO> http = objectMapper.readValue(registerJson, typeRef);

        assertThat(http.status()).isFalse();

        assertThat(http.data()).isNull();
    }

    @Test
    void shouldReturnTrueWhenRuleExists() throws Exception {

        String token = this.createUser();

        UrlAccessRuleModel rule =
                this.createUrlAccessRule();

        MvcResult result = mockMvc.perform(
                        get(this.URL + "/exists")
                                .param("urlId", rule.getUrlId().toString())
                                .param("type", rule.getType().name())
                                .param("ruleValue", rule.getRuleValue())
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String registerJson =
                result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<Boolean>> typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> http =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(http.status()).isTrue();

        assertThat(http.data()).isTrue();
    }

    @Test
    void shouldReturnFalseWhenRuleDoesNotExist() throws Exception {

        String token = this.createUser();

        MvcResult result = mockMvc.perform(
                        get(this.URL + "/exists")
                                .param("urlId", String.valueOf(generator.nextId()))
                                .param(
                                        "type",
                                        UrlAccessRuleTypeEnum.RATE_LIMIT.name()
                                )
                                .param(
                                        "ruleValue",
                                        "not-found@gmail.com"
                                )
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String registerJson =
                result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<Boolean>> typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> http =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(http.status()).isTrue();

        assertThat(http.data()).isFalse();
    }

}
