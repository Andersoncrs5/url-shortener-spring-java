package com.write.api.adapters.in.web.controller.util.helps;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;
    private final SnowflakeIdGenerator idGen;

    public UserTest loginMaster() {
        return this.createNewUser();
    }

    public UserTest createNewUser() {
        try {
            String URL = "/v1/auth/";
            var key = UUID.randomUUID();

            CreateUserDTO dto = new CreateUserDTO(
                    "user " + key ,
                    "user" + key + "@gmail.com",
                    "12345678"
            );

            MvcResult result = mockMvc.perform(post(URL + "/register")
                            .contentType(MediaType.APPLICATION_JSON)
                            .header("X-Idempotency-Key", key)
                            .content(objectMapper.writeValueAsString(dto)))
                    .andExpect(status().isCreated())
                    .andReturn();

            String registerJson = result.getResponse().getContentAsString();
            TypeReference<ResponseHttp<AuthTokenResponseDTO>> typeRef =
                    new TypeReference<>() {};

            ResponseHttp<AuthTokenResponseDTO> response =
                    objectMapper.readValue(registerJson, typeRef);

            assertThat(response.status()).isEqualTo(true);
            assertThat(response.message()).isNotBlank();
            assertThat(response.data().token()).isNotBlank();
            assertThat(response.data().refreshToken()).isNotBlank();
            assertThat(response.data().user().getName()).isNotBlank().isEqualTo(dto.name());
            assertThat(response.data().user().getEmail()).isNotBlank().isEqualTo(dto.email());

            return new UserTest(
                    dto,
                    response.data()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public UrlTagResponseDTO createUrlTag(UserTest user) throws Exception {
        var key = UUID.randomUUID().toString();
        String URL = "/v1/url-tag";

        CreateUrlTagDTO dto = new CreateUrlTagDTO(
                "tag simple num: " + key,
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
                .andExpect(status().isCreated())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UrlTagResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagResponseDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("Tag created");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isNotNull().isNotZero().isNotNegative();
        assertThat(response.data().name()).isEqualTo(dto.name());
        assertThat(response.data().slug()).isEqualTo(dto.slug());
        assertThat(response.data().color()).isEqualTo(dto.color());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().parentId()).isEqualTo(dto.parentId());
        assertThat(response.data().active()).isEqualTo(dto.active());

        return response.data();
    }

    public UrlResponseDTO createUrl(UserTest user, String password) throws Exception {
        var key = UUID.randomUUID().toString();
        String URL = "/v1/url";

        CreateUrlDTO dto = new CreateUrlDTO(
                "https://example.com/article/" + idGen.nextId(),
                "My title",
                "Any desc",
                "https://example.com/favicon.ico",
                "example.com",
                UrlAccessTypeEnum.PUBLIC,
                password,
                LocalDateTime.now().plusDays(7)
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UrlResponseDTO>> typeRef =
                new TypeReference<>() {};

        var response = objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().originalUrl()).isEqualTo(dto.originalUrl());
        assertThat(response.data().title()).isEqualTo(dto.title());
        assertThat(response.data().description()).isEqualTo(dto.description());
        assertThat(response.data().faviconUrl()).isEqualTo(dto.faviconUrl());
        assertThat(response.data().domain()).isEqualTo(dto.domain());
        return response.data();
    }

    public UrlTagLinkDTO createLinkTagToUrl(
            UserTest user,
            UrlResponseDTO url,
            UrlTagResponseDTO urlTag
    ) throws Exception {
        var key = UUID.randomUUID().toString();
        String URL = "/v1/url-tag-link";

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
                .andExpect(status().isCreated())
                .andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<UrlTagLinkDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlTagLinkDTO> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isEqualTo(true);
        assertThat(response.message()).isNotBlank().containsIgnoringCase("linked");
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.data()).isNotNull();
        assertThat(response.data().id()).isNotNegative().isNotZero();
        assertThat(response.data().urlId()).isEqualTo(url.id());
        assertThat(response.data().tagId()).isEqualTo(urlTag.id());
        assertThat(response.data().sortOrder()).isEqualTo(dto.sortOrder());
        assertThat(response.data().note()).isEqualTo(dto.note());
        assertThat(response.data().primaryTag()).isEqualTo(dto.primaryTag());

        return response.data();
    }

}
