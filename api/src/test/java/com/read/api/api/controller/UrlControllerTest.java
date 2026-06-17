package com.read.api.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.read.api.api.base.BaseIntegrationTest;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.url.UrlDTO;
import com.read.api.api.utils.PageResponse;
import com.read.api.domain.model.UrlModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UrlControllerTest extends BaseIntegrationTest {
    private final String URL = "/v1/url";

    @Test
    void shouldFindAllUrl() throws Exception {
        String token = this.createUser();
        ArrayList<UrlModel> rules = this.createUrlMany(10);

        MvcResult result = mockMvc.perform(get(this.URL)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<PageResponse<UrlDTO>> typeRef = new TypeReference<>() {};

        objectMapper.readValue(registerJson, typeRef);
    }

    @Test
    void shouldReturnUrlWhenFindById() throws Exception {

        String token = createUser();

        UrlModel url = createUrl();

        MvcResult result =
                mockMvc.perform(
                                get(URL + "/" + url.getId())
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

        TypeReference<ResponseHTTP<UrlDTO>>
                typeRef =
                new TypeReference<>() {};

        ResponseHTTP<UrlDTO> response =
                objectMapper.readValue(
                        json,
                        typeRef
                );

        assertThat(response.status())
                .isTrue();

        assertThat(response.data())
                .isNotNull();

        assertThat(response.data().getId())
                .isEqualTo(url.getId());
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

        TypeReference<ResponseHTTP<UrlDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<UrlDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnUrlWhenRedirectShortCode() throws Exception {
        UrlModel url = createUrl();

        String shortCode = url.getShortCode();

        MvcResult result = mockMvc.perform(
                        get(URL + "/r/" + shortCode)
                                .header("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36")
                                .header("X-Forwarded-For", "203.0.113.195")
                                .header("X-Url-Password", "url_secret_pass")
                )
                .andExpect(status().is3xxRedirection())
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHTTP<UrlDTO>> typeRef = new TypeReference<>() {};
        ResponseHTTP<UrlDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.data()).isNotNull();
        assertThat(response.data().getId()).isEqualTo(url.getId());
        assertThat(response.data().getShortCode()).isEqualTo(shortCode);
    }

    @Test
    void shouldReturnNotFoundWhenRedirectShortCodeDoesNotExist() throws Exception {
        String invalidShortCode = "non-existent-code-123";

        MvcResult result = mockMvc.perform(
                        get(URL + "/r/" + invalidShortCode)
                                .header("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 17_0 like Mac OS X) AppleWebKit/605.1.15")
                                .header("X-Forwarded-For", "192.168.1.1")
                )
                .andExpect(status().isNotFound()) // Espera falhar no UseCase retornando 404
                .andReturn();

        String json = result.getResponse().getContentAsString();
        TypeReference<ResponseHTTP<UrlDTO>> typeRef = new TypeReference<>() {};
        ResponseHTTP<UrlDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isFalse();
        assertThat(response.data()).isNull();
    }

}
