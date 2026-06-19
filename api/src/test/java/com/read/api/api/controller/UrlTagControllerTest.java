package com.read.api.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.read.api.api.base.BaseIntegrationTest;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.tag.UrlTagDTO;
import com.read.api.api.utils.PageResponse;
import com.read.api.domain.model.UrlTagModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UrlTagControllerTest extends BaseIntegrationTest {
    private final String URL = "/v1/url-tag";

    @Test
    void shouldFindAllUrlTag() throws Exception {

        String token = createUser();

        createUrlTags(10);

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

        String json = result.getResponse().getContentAsString();

        TypeReference<PageResponse<UrlTagDTO>> typeRef = new TypeReference<>() {};

        PageResponse<UrlTagDTO> page = objectMapper.readValue(json, typeRef);

        assertThat(page.getContent())
                .isNotEmpty();
    }

    @Test
    void shouldReturnUrlTagWhenFindById() throws Exception {

        String token = createUser();

        UrlTagModel rule = createUrlTag();

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

        TypeReference<ResponseHTTP<UrlTagDTO>>
                typeRef =
                new TypeReference<>() {};

        ResponseHTTP<UrlTagDTO> response =
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

        TypeReference<ResponseHTTP<UrlTagDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<UrlTagDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturnTrueWhenNameExists() throws Exception {

        String token = createUser();

        UrlTagModel tag = createUrlTag();

        MvcResult result =
                mockMvc.perform(
                                get(URL + "/name-exists")
                                        .param("name", tag.getName())
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

        TypeReference<ResponseHTTP<Boolean>>
                typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(
                        json,
                        typeRef
                );

        assertThat(response.status())
                .isTrue();

        assertThat(response.data())
                .isTrue();

        assertThat(response.message())
                .isEqualTo("Name checked");
    }

    @Test
    void shouldReturnFalseWhenNameDoesNotExist() throws Exception {

        String token = createUser();

        MvcResult result =
                mockMvc.perform(
                                get(URL + "/name-exists")
                                        .param(
                                                "name",
                                                "not-found-" + generator.nextId()
                                        )
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

        TypeReference<ResponseHTTP<Boolean>>
                typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(
                        json,
                        typeRef
                );

        assertThat(response.status())
                .isTrue();

        assertThat(response.data())
                .isFalse();

        assertThat(response.message())
                .isEqualTo("Name checked");
    }

    @Test
    void shouldReturnTrueWhenSlugExists() throws Exception {

        String token = createUser();

        UrlTagModel tag = createUrlTag();

        MvcResult result =
                mockMvc.perform(
                                get(URL + "/slug-exists")
                                        .param("slug", tag.getSlug())
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

        TypeReference<ResponseHTTP<Boolean>>
                typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(
                        json,
                        typeRef
                );

        assertThat(response.status())
                .isTrue();

        assertThat(response.data())
                .isTrue();

        assertThat(response.message())
                .isEqualTo("Slug checked");
    }

    @Test
    void shouldReturnFalseWhenSlugDoesNotExist() throws Exception {

        String token = createUser();

        MvcResult result =
                mockMvc.perform(
                                get(URL + "/slug-exists")
                                        .param(
                                                "slug",
                                                "not-found-" + generator.nextId()
                                        )
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

        TypeReference<ResponseHTTP<Boolean>>
                typeRef =
                new TypeReference<>() {};

        ResponseHTTP<Boolean> response =
                objectMapper.readValue(
                        json,
                        typeRef
                );

        assertThat(response.status())
                .isTrue();

        assertThat(response.data())
                .isFalse();

        assertThat(response.message())
                .isEqualTo("Slug checked");
    }

}
