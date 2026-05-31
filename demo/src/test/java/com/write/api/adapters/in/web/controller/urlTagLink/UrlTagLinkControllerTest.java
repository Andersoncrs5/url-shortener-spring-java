package com.write.api.adapters.in.web.controller.urlTagLink;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
import com.write.api.application.shared.validation.ValidationErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UrlTagLinkControllerTest {
    private final String URL = "/v1/url-tag-link";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Test
    void shouldLinkTagToUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);
        this.helper.createLinkTagToUrl(user, url, urlTag);
    }

    @Test
    void shouldFailBecauseUrlNotFoundTheLinkTagToUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                urlTag.id(),
                urlTag.id(),
                (short) 1,
                "Any notes",
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("url not found");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseUrlTagNotFoundTheLinkTagToUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        var key = UUID.randomUUID().toString();

        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                url.id(),
                url.id(),
                (short) 1,
                "Any notes",
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("tag not found");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseTagAlreadyExistsInUrlTheLinkTagToUrl() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);
        this.helper.createLinkTagToUrl(user, url, urlTag);

        var key = UUID.randomUUID().toString();

        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                url.id(),
                urlTag.id(),
                (short) 1,
                "Any notes",
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isConflict()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response = objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(false);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("Tag already present in url");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailLinkTagToUrlBecauseIdempotencyKeyIsMissing() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);

        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                url.id(),
                urlTag.id(),
                (short) 1,
                "Any notes",
                true
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailLinkTagToUrlBecauseUserIsUnauthorized() throws Exception {
        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                1L,
                1L,
                (short) 1,
                "Any notes",
                true
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", UUID.randomUUID().toString())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailLinkTagToUrlBecauseUrlIdIsInvalid() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                0L,
                urlTag.id(),
                (short) 1,
                "Any notes",
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<ValidationErrorResponse>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ValidationErrorResponse> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldFailLinkTagToUrlBecauseNoteExceededLimit() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        String note = "a".repeat(501);

        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                url.id(),
                urlTag.id(),
                (short) 1,
                note,
                true
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<ValidationErrorResponse>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<ValidationErrorResponse> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldFailLinkTagToUrlBecauseTagIdIsInvalid() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);

        var key = UUID.randomUUID().toString();

        CreateUrlTagLinkDTO dto = new CreateUrlTagLinkDTO(
                url.id(),
                0L,
                (short) 1,
                "Any notes",
                true
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailLinkTagToUrlBecausePrimaryTagIsNull() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlTagResponseDTO urlTag = this.helper.createUrlTag(user);

        var key = UUID.randomUUID().toString();

        String body = """
        {
          "urlId": %d,
          "tagId": %d,
          "sortOrder": 1,
          "note": "Any notes",
          "primaryTag": null
        }
        """.formatted(url.id(), urlTag.id());

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(body))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUrlTagLink() throws Exception {
        UserTest user = this.helper.createNewUser();

        UrlResponseDTO url =
                this.helper.createUrl(user, null);

        UrlTagResponseDTO tag =
                this.helper.createUrlTag(user);

        UrlTagLinkDTO link =
                this.helper.createLinkTagToUrl(user, url, tag);

        String key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(
                        delete(URL + "/" + link.id())
                                .header("X-Idempotency-Key", key)
                                .header(
                                        "Authorization",
                                        "Bearer " + user.tokens().token()
                                )
                )
                .andExpect(status().isOk())
                .andReturn();

        String registerJson =
                result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isTrue();

        assertThat(response.message())
                .isNotBlank()
                .containsIgnoringCase("unlinked");

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailDeleteUrlTagLinkBecauseNotFound() throws Exception {
        UserTest user = this.helper.createNewUser();

        String key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(
                        delete(URL + "/" + user.tokens().user().getId())
                                .header("X-Idempotency-Key", key)
                                .header(
                                        "Authorization",
                                        "Bearer " + user.tokens().token()
                                )
                )
                .andExpect(status().isNotFound())
                .andReturn();

        String registerJson =
                result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
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
    void shouldFailDeleteUrlTagLinkBecauseIdempotencyKeyIsMissing() throws Exception {
        UserTest user = this.helper.createNewUser();

        UrlResponseDTO url =
                this.helper.createUrl(user, null);

        UrlTagResponseDTO tag =
                this.helper.createUrlTag(user);

        UrlTagLinkDTO link =
                this.helper.createLinkTagToUrl(user, url, tag);

        mockMvc.perform(
                        delete(URL + "/" + link.id())
                                .header(
                                        "Authorization",
                                        "Bearer " + user.tokens().token()
                                )
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailDeleteUrlTagLinkBecauseIdIsInvalid() throws Exception {
        UserTest user = this.helper.createNewUser();

        String key = UUID.randomUUID().toString();

        MvcResult result = mockMvc.perform(
                        delete(URL + "/0")
                                .header("X-Idempotency-Key", key)
                                .header(
                                        "Authorization",
                                        "Bearer " + user.tokens().token()
                                )
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String registerJson =
                result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response).isNotNull();
    }

    @Test
    void shouldFailDeleteUrlTagLinkBecauseUserIsUnauthorized() throws Exception {

        String key = UUID.randomUUID().toString();

        mockMvc.perform(
                        delete(URL + "/1")
                                .header("X-Idempotency-Key", key)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldUpdateUrlTagLink() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlTagResponseDTO tag = this.helper.createUrlTag(user);
        UrlTagLinkDTO link = this.helper.createLinkTagToUrl(user, url, tag);

        String key = UUID.randomUUID().toString();

        UpdateUrlTagLinkDTO dto = new UpdateUrlTagLinkDTO(
                (short) 2,
                "updated note",
                false
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + link.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlTagLinkDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagLinkDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.message())
                .isNotBlank()
                .containsIgnoringCase("updated");

        assertThat(response.traceId())
                .isEqualTo(key);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isEqualTo(link.id());
        assertThat(response.data().urlId()).isEqualTo(url.id());
        assertThat(response.data().tagId()).isEqualTo(tag.id());
        assertThat(response.data().sortOrder()).isEqualTo(dto.sortOrder());
        assertThat(response.data().note()).isEqualTo(dto.note());
        assertThat(response.data().primaryTag()).isEqualTo(dto.primaryTag());
    }

    @Test
    void shouldFailBecauseUrlTagLinkNotFound() throws Exception {
        UserTest user = this.helper.createNewUser();

        String key = UUID.randomUUID().toString();

        UpdateUrlTagLinkDTO dto = new UpdateUrlTagLinkDTO(
                (short) 2,
                "updated note",
                false
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + user.tokens().user().getId())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", key)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
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
                .isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailBecauseIdempotencyKeyIsMissing() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlResponseDTO url = this.helper.createUrl(user, null);
        UrlTagResponseDTO tag = this.helper.createUrlTag(user);
        UrlTagLinkDTO link = this.helper.createLinkTagToUrl(user, url, tag);

        UpdateUrlTagLinkDTO dto = new UpdateUrlTagLinkDTO(
                (short) 2,
                "updated note",
                false
        );

        mockMvc.perform(
                        patch(URL + "/" + link.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFailBecauseUserIsUnauthorized() throws Exception {
        UpdateUrlTagLinkDTO dto = new UpdateUrlTagLinkDTO(
                (short) 2,
                "updated note",
                false
        );

        mockMvc.perform(
                        patch(URL + "/" + 34553723654735634L)
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldFailBecauseIdIsInvalid() throws Exception {
        UserTest user = this.helper.createNewUser();

        UpdateUrlTagLinkDTO dto = new UpdateUrlTagLinkDTO(
                (short) 2,
                "updated note",
                false
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/0")
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isBadRequest())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
    }

    @Test
    void shouldFailBecauseUrlNotFoundInUpdate() throws Exception {
        UserTest user = this.helper.createNewUser();
        UrlTagResponseDTO tag = this.helper.createUrlTag(user);
        UrlTagLinkDTO link = this.helper.createLinkTagToUrl(user, this.helper.createUrl(user, null), tag);

        UpdateUrlTagLinkDTO dto = new UpdateUrlTagLinkDTO(
                (short) 2,
                "updated note",
                false
        );

        MvcResult result = mockMvc.perform(
                        patch(URL + "/" + link.id())
                                .contentType(MediaType.APPLICATION_JSON)
                                .header("X-Idempotency-Key", UUID.randomUUID().toString())
                                .header("Authorization", "Bearer " + user.tokens().token())
                                .content(objectMapper.writeValueAsString(dto))
                )
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);
    }

}
