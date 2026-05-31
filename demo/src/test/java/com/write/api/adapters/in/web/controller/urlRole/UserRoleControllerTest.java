package com.write.api.adapters.in.web.controller.urlRole;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.write.api.adapters.in.web.controller.util.classes.UserTest;
import com.write.api.adapters.in.web.controller.util.helps.HelperTest;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.dto.userRole.UserRoleDTO;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.out.repository.IRoleRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserRoleControllerTest {
    private final String URL = "/v1/user-role";

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private HelperTest helper;
    @Autowired private SnowflakeIdGenerator idGen;
    @Autowired private IRoleRepository roleRepository;

    @Test
    void shouldAddRoleAdmToUser() throws Exception {
        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();
        UserTest user = this.helper.createNewUser();
        this.helper.addRoleAdmToUser(superAdm, user);
    }

    @Test
    void shouldFailTheAddRoleAdmToUserBecauseRoleNotFound() throws Exception {
        var key = UUID.randomUUID().toString();

        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();
        UserTest user = this.helper.createNewUser();

        CreateUserRoleDTO dto = new CreateUserRoleDTO(
                user.tokens().user().getId(),
                idGen.nextId()
        );

        MvcResult result = mockMvc.perform(post(URL + "/add-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + superAdm.token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UserRoleDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserRoleDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailTheAddRoleAdmToUserBecauseUserNotFound() throws Exception {
        var key = UUID.randomUUID().toString();
        RoleModel adminRole = roleRepository.findByNameIgnoreCase("ADMIN").orElse(null);
        assert adminRole != null;

        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();
        UserTest user = this.helper.createNewUser();

        CreateUserRoleDTO dto = new CreateUserRoleDTO(
                idGen.nextId(),
                adminRole.getId()
        );

        MvcResult result = mockMvc.perform(post(URL + "/add-role")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + superAdm.token())
                        .header("X-Idempotency-Key", key)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHttp<UserRoleDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<UserRoleDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    // DELETE
    @Test
    void shouldDeleteUserRole() throws Exception {
        var key = UUID.randomUUID().toString();

        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();
        UserTest user = this.helper.createNewUser();
        UserRoleDTO userRoleDTO = this.helper.addRoleAdmToUser(superAdm, user);

        MvcResult result = mockMvc.perform(delete(URL + "/remove-role/" + userRoleDTO.id())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + superAdm.token())
                        .header("X-Idempotency-Key", key))
                .andExpect(status().isOk())
                .andReturn();

        TypeReference<ResponseHttp<Void>> typeRef =
                new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(result.getResponse().getContentAsString(), typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isTrue();
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFailTheDeleteUserRoleBecauseUserRoleNotFound() throws Exception {
        var key = UUID.randomUUID().toString();

        AuthTokenResponseDTO superAdm = this.helper.loginSuperAdm();

        MvcResult result = mockMvc.perform(delete(URL + "/remove-role/" + idGen.nextId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + superAdm.token())
                        .header("X-Idempotency-Key", key))
                .andExpect(status().isNotFound())
                .andReturn();

        TypeReference<ResponseHttp<Void>> typeRef = new TypeReference<>() {};

        ResponseHttp<Void> response =
                objectMapper.readValue(result.getResponse().getContentAsString(), typeRef);

        assertThat(response).isNotNull();
        assertThat(response.status()).isFalse();
        assertThat(response.traceId()).isNotNull().isEqualTo(key);

        assertThat(response.data()).isNull();
    }

}
