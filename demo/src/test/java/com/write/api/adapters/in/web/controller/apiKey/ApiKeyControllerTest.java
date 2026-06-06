package com.write.api.adapters.in.web.controller.apiKey;

import com.write.api.adapters.in.web.controller.BaseControllerTest;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.application.dto.apiKey.CreateApiKeyDTO;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class ApiKeyControllerTest extends BaseControllerTest {

    private final String URL = "/v1/api-key";

    @Test
    void shouldCreateNewKey() throws Exception {
        UserTest user = this.helper.createNewUser();

        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();

        this.helper.createApiKey(superAdm, user);
    }

    @Test
    void shouldSuccessTheTestApiKey() throws Exception {
        UserTest user = this.helper.createNewUser();

        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();

        String key = this.helper.createApiKey(superAdm, user);

        MvcResult result = mockMvc.perform(get(URL + "/test")
                .header("Authorization", "Bearer " + superAdm.token())
                .header("X-API-KEY", key)
        ).andExpect(status().isOk()).andReturn();

        String registerJson = result.getResponse().getContentAsString();
        assertThat(registerJson).isEqualTo(key);
    }

    @Test
    void shouldFailTheCreateNewKeyBecauseIdempotencyMissed() throws Exception {
        UserTest user = this.helper.createNewUser();
        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();

        CreateApiKeyDTO dto = new CreateApiKeyDTO(
                "sei la 123" + UUID.randomUUID(),
                LocalDateTime.now().plusDays(23),
                true,
                user.tokens().user().getId()
        );

        mockMvc.perform(post(URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto))
                        .header("Authorization", "Bearer " + superAdm.token())
                ).andExpect(status().isBadRequest());
    }

}
