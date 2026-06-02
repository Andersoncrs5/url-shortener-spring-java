package com.write.api.adapters.in.web.controller.apiKey;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class ApiKeyControllerTest {

    private final String URL = "/v1/api-key";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;

    @Test
    void shouldCreateNewKey() throws Exception {
        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();

        this.helper.createApiKey(superAdm);
    }

    @Test
    void shouldSuccessTheTestApiKey() throws Exception {
        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();

        String key = this.helper.createApiKey(superAdm);

        MvcResult result = mockMvc.perform(get(URL + "/test")
                .header("Authorization", "Bearer " + superAdm.token())
                .header("X-API-KEY", key)
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        assertThat(registerJson).isEqualTo(key);
    }

    @Test
    void shouldFailTheCreateNewKeyBecauseIdempotencyMissed() throws Exception {
        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();

        CreateApiKeyDTO dto = new CreateApiKeyDTO(
                "sei la 123" + UUID.randomUUID(),
                LocalDateTime.now().plusDays(23),
                true
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + superAdm.token())
                ).andExpect(status().isBadRequest());
    }



}
