package com.read.api.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.read.api.api.base.BaseIntegrationTest;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.read.api.api.utils.PageResponse;
import com.read.api.domain.model.UrlRedirectRuleModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UrlRedirectRuleControllerTest extends BaseIntegrationTest {

    private final String URL = "/v1/url-redirect-rule";

    @Test
    void shouldFindAllUrlRedirectRule() throws Exception {

        String token = createUser();

        createManyUrlRedirectRule(10);

        MvcResult result =
                mockMvc.perform(
                                get(URL)
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        String json =
                result.getResponse()
                        .getContentAsString();

        TypeReference<PageResponse<UrlRedirectRuleDTO>>
                typeRef =
                new TypeReference<>() {};

        PageResponse<UrlRedirectRuleDTO> page =
                objectMapper.readValue(
                        json,
                        typeRef
                );

        assertThat(page.getContent())
                .isNotEmpty();
    }

    @Test
    void shouldReturnUrlRedirectRuleWhenFindById() throws Exception {

        String token = createUser();

        UrlRedirectRuleModel rule = createUrlRedirectRule();

        MvcResult result =
                mockMvc.perform(
                                get(URL + "/" + rule.getId())
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(status().isOk())
                        .andReturn();

        String json =
                result.getResponse()
                        .getContentAsString();

        TypeReference<ResponseHTTP<UrlRedirectRuleDTO>>
                typeRef =
                new TypeReference<>() {};

        ResponseHTTP<UrlRedirectRuleDTO> response =
                objectMapper.readValue(
                        json,
                        typeRef
                );

        assertThat(response.status())
                .isTrue();

        assertThat(response.data())
                .isNotNull();

        assertThat(response.data().getId())
                .isEqualTo(rule.getId());
    }

    @Test
    void shouldReturnNotFoundWhenFindById() throws Exception {

        String token = createUser();

        MvcResult result =
                mockMvc.perform(
                                get(URL + "/" + generator.nextId())
                                        .header(
                                                "Authorization",
                                                "Bearer " + token
                                        )
                        )
                        .andExpect(status().isNotFound())
                        .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<UrlRedirectRuleDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<UrlRedirectRuleDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.data()).isNull();
    }
}