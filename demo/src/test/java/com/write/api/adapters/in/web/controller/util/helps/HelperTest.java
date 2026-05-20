package com.write.api.adapters.in.web.controller.util.helps;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Service
@RequiredArgsConstructor
public class HelperTest {

    private final MockMvc mockMvc;
    private final ObjectMapper objectMapper;

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

}
