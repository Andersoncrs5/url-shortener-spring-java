package com.write.api.adapters.in.web.controller.util.helps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.dto.url.UrlResponseDTO;
import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.dto.urlAccessRule.UrlAccessRuleResponseDTO;
import com.write.api.application.dto.urlRedirectRule.CreateUrlRedirectRuleDTO;
import com.write.api.application.dto.urlRedirectRule.UrlRedirectRuleDTO;
import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.dto.urlTag.UrlTagResponseDTO;
import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.dto.urlTagLink.UrlTagLinkDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.dto.userRole.UserRoleDTO;
import com.write.api.core.domain.enums.*;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.help.RandomStringGenerator;
import com.write.api.ports.out.repository.IRoleRepository;
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
    private final IRoleRepository roleRepository;

    public String createApiKey(
            AuthTokenResponseDTO superAdm,
            UserTest user
    ) throws Exception {
        var key = UUID.randomUUID().toString();
        String URL = "/v1/api-key";

        CreateApiKeyDTO dto = new CreateApiKeyDTO(
                "sei la 123" + key,
                LocalDateTime.now().plusDays(23),
                true,
                user.tokens().user().getId()
        );

        MvcResult result = mockMvc.perform(post(URL)
                .contentType(MediaType.APPLICATION_JSON)
                .header("X-Idempotency-Key", key)
                .content(objectMapper.writeValueAsString(dto))
                .header("Authorization", "Bearer " + superAdm.token())
        ).andExpect(status().isCreated()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        TypeReference<ResponseHttp<String>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<String> response =
                objectMapper.readValue(registerJson, typeRef);

        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotBlank().isEqualTo(key);
        assertThat(response.message()).isNotBlank();
        assertThat(response.data()).isNotBlank();

        return response.data();
    }

    public UrlAccessRuleResponseDTO addAccessRuleToUrl(
            UrlResponseDTO url,
            UserTest user
    ) throws Exception {
        String URL = "/v1/url-access-rule";
        var key = UUID.randomUUID().toString();

        CreateUrlAccessRuleDTO dto = new CreateUrlAccessRuleDTO(
                url.id(),
                UrlAccessRuleTypeEnum.MAX_CLICKS,
                "34324324",
                LocalDateTime.now().plusDays(1)
        );

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlAccessRuleResponseDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlAccessRuleResponseDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().urlId()).isNotNull().isEqualTo(url.id());

        return response.data();
    }

    public UserRoleDTO addRoleAdmToUser(
            AuthTokenResponseDTO superAdm,
            UserTest user
    ) throws Exception {
        String URL = "/v1/user-role";
        var key = UUID.randomUUID().toString();

        RoleModel adminRole = roleRepository.findByNameIgnoreCase("ADMIN").orElse(null);
        assert adminRole != null;

        CreateUserRoleDTO dto = new CreateUserRoleDTO(
                user.tokens().user().getId(),
                adminRole.getId()
        );

        MvcResult result = mockMvc.perform(post(URL + "/add-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + superAdm.token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UserRoleDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserRoleDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNotNull();
        assertThat(response.data().userId()).isNotNull().isEqualTo(user.tokens().user().getId());
        assertThat(response.data().roleId()).isNotNull().isEqualTo(adminRole.getId());

        return response.data();
    }

    public AuthTokenResponseDTO loginSuperAdm() throws Exception {
        String URL = "/v1/auth/";
        var key = UUID.randomUUID();

        LoginUserDTO dto = new LoginUserDTO(
                "superadm@gmail.com",
                "12345678"
        );

        MvcResult result = mockMvc.perform(post(URL + "/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk())
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
        assertThat(response.data().user().getEmail()).isNotBlank().isEqualTo(dto.email());

        return response.data();
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
        String slug = "tag-slug-" + RandomStringGenerator.random(10)
                .toLowerCase().replaceAll("[^a-z0-9]", "");

        CreateUrlTagDTO dto = new CreateUrlTagDTO(
                "tag simple num: " + key,
                slug,
                "#6366F1",
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

    public UrlRedirectRuleDTO createUrlRedirectRule(
            UserTest user,
            UrlResponseDTO url
    ) throws Exception {
        String URL = "/v1/url-redirect-rule";
        var key = UUID.randomUUID().toString();

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

        MvcResult result = mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + user.tokens().token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UrlRedirectRuleDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UrlRedirectRuleDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();

        assertThat(response.status()).isTrue();
        assertThat(response.message()).isNotBlank();
        assertThat(response.traceId()).isEqualTo(key);

        UrlRedirectRuleDTO data = response.data();

        assertThat(data).isNotNull();

        assertThat(data.id()).isNotNull();
        assertThat(data.urlId()).isEqualTo(url.id());

        assertThat(data.countryCode()).isEqualTo(dto.countryCode());
        assertThat(data.region()).isEqualTo(dto.region());

        assertThat(data.continent()).isEqualTo(dto.continent());
        assertThat(data.os()).isEqualTo(dto.os());
        assertThat(data.browser()).isEqualTo(dto.browser());

        assertThat(data.matchType()).isEqualTo(dto.matchType());

        assertThat(data.redirectUrl()).isEqualTo(dto.redirectUrl());

        assertThat(data.priority()).isEqualTo(dto.priority());

        assertThat(data.active()).isEqualTo(dto.active());

        assertThat(data.startAt()).isNotNull();
        assertThat(data.endAt()).isNotNull();

        assertThat(data.createdAt()).isNotNull();
        assertThat(data.updatedAt()).isNotNull();

        return data;
    }

}
