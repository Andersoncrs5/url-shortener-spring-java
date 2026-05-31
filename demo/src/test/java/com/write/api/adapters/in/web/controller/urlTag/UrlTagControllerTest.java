package com.write.api.adapters.in.web.controller.urlTag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlTagControllerTest {
    private final String URL = "/v1/url-tag";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Test
    void shouldCreateUrlTag() throws Exception {
        UserTest user = this.helper.createNewUser();
        this.helper.createUrlTag(user);
    }

    @Test
    void shouldReturn400BecauseIdempotentKeyIsMissed() throws Exception {
        UserTest user = this.helper.createNewUser();
        var key = UUID.randomUUID().toString();

        CreateUrlTagDTO dto = new CreateUrlTagDTO(
                "tag simple num: " + key,
                "tag-slug-simple-num: " + key,
                "#0000000",
                "any desc",
                null,
                true
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn409BecauseNameAlready() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);
        var key = UUID.randomUUID().toString();

        CreateUrlTagDTO dto = new CreateUrlTagDTO(
                urlTag.name(),
                "tag-slug-simple-num: " + key,
                "#0000000",
                "any desc",
                null,
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase(dto.name());
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturn409BecauseSlugAlready() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);
        var key = UUID.randomUUID().toString();

        CreateUrlTagDTO dto = new CreateUrlTagDTO(
                urlTag.name() + key,
                urlTag.slug(),
                "#0000000",
                "any desc",
                null,
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase(dto.slug());
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    // DELETE
    @Test
    void shouldDelUrlTag() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);
        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/" + urlTag.id())
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
    void shouldReturn404DelUrlTag() throws Exception {
        UserTest user = this.helper.createNewUser();
        var key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(delete(URL + "/" + (user.tokens().user().getId()))
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

    // PATCH
    @Test
    void shouldUpdateUrlTag() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlTagResponseDTO created = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        UpdateUrlTagDTO dto = new UpdateUrlTagDTO(
                "updated-name-" + key,
                "updated-slug-" + key,
                "#FFFFFF",
                "updated description",
                null,
                false
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

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.message())
                .containsIgnoringCase("updated");

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNotNull();

        assertThat(response.data().id())
                .isEqualTo(created.id());

        assertThat(response.data().name())
                .isEqualTo(dto.name());

        assertThat(response.data().slug())
                .isEqualTo(dto.slug());

        assertThat(response.data().color())
                .isEqualTo(dto.color());

        assertThat(response.data().description())
                .isEqualTo(dto.description());

        assertThat(response.data().active())
                .isEqualTo(dto.active());
    }

    @Test
    void shouldReturn404WhenUpdateUrlTagNotFound() throws Exception {
        UserTest user = this.helper.createNewUser();

        var key = UUID.randomUUID().toString();

        UpdateUrlTagDTO dto = new UpdateUrlTagDTO(
                "updated-name",
                "updated-slug",
                "#FFFFFF",
                "updated description",
                null,
                true
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + Long.MAX_VALUE)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isNotFound())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.message())
                .containsIgnoringCase("not found");

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturn409WhenUpdateSlugAlreadyExists() throws Exception {
        UserTest user = this.helper.createNewUser();

        UrlTagResponseDTO first = this.helper.createUrlTag(user);
        UrlTagResponseDTO second = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        UpdateUrlTagDTO dto = new UpdateUrlTagDTO(
                second.name(),
                first.slug(),
                "#FFFFFF",
                "updated description",
                null,
                true
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + second.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isConflict())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.message())
                .containsIgnoringCase(dto.slug());

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturn409WhenUpdateNameAlreadyExists() throws Exception {
        UserTest user = this.helper.createNewUser();

        UrlTagResponseDTO first = this.helper.createUrlTag(user);
        UrlTagResponseDTO second = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        UpdateUrlTagDTO dto = new UpdateUrlTagDTO(
                first.name(),
                "new-slug-" + key,
                "#FFFFFF",
                "updated description",
                null,
                true
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + second.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isConflict())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.message())
                .containsIgnoringCase(dto.name());

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturn409WhenParentIdIsSameTagId() throws Exception {
        UserTest user = this.helper.createNewUser();

        UrlTagResponseDTO tag = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        UpdateUrlTagDTO dto = new UpdateUrlTagDTO(
                "updated-name",
                "updated-slug",
                "#FFFFFF",
                "updated description",
                tag.id(),
                true
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + tag.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isConflict())
                .andReturn();

        String responseJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(responseJson, typeRef);

        assertThat(response.status()).isFalse();

        assertThat(response.message())
                .containsIgnoringCase("parent");

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldReturn400WhenIdempotencyKeyIsMissingOnUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();

        UrlTagResponseDTO tag = this.helper.createUrlTag(user);

        UpdateUrlTagDTO dto = new UpdateUrlTagDTO(
                "updated-name",
                "updated-slug",
                "#FFFFFF",
                "updated description",
                null,
                true
        );

        mockMvc.perform(
                        patch(URL + "/" + tag.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

}
