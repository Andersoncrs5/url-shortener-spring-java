package com.read.api.api.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.read.api.api.base.BaseIntegrationTest;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.deadLetterEvent.DeadLetterEventDTO;
import com.read.api.api.utils.PageResponse;
import com.read.api.domain.enums.DeadLetterStatus;
import com.read.api.domain.model.DeadLetterEventModel;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DeadLetterEventControllerTest extends BaseIntegrationTest {
    private static final String URL = "/v1/dead-letter-event";

    @Test
    void shouldFindAllDeadLetterEvents() throws Exception {
        deadLetterRepository.deleteAll();

        String token = createUser();

        ArrayList<DeadLetterEventModel> events = createManyDeadLetterEvents(10);

        MvcResult result = mockMvc.perform(
                        get(URL)
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<PageResponse<DeadLetterEventDTO>> typeRef = new TypeReference<>() {};

        PageResponse<DeadLetterEventDTO> page = objectMapper.readValue(json, typeRef);

        assertThat(page).isNotNull();
        assertThat(page.getContent()).hasSize(events.size());

        deadLetterRepository.deleteAll();
    }

    @Test
    void shouldReturnDeadLetterEventWhenFindById() throws Exception {

        String token = createUser();
        DeadLetterEventModel event = createDeadLetterEvent();

        MvcResult result = mockMvc.perform(
                        get(URL + "/" + event.getId())
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json =
                result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<DeadLetterEventDTO>> typeRef =
                new TypeReference<>() {};

        ResponseHTTP<DeadLetterEventDTO> response =
                objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isTrue();

        assertThat(response.data()).isNotNull();
        assertThat(response.data().getId()).isEqualTo(event.getId());
        assertThat(response.data().getEventId()).isEqualTo(event.getEventId());
        assertThat(response.data().getStatus()).isEqualTo(event.getStatus());
    }

    @Test
    void shouldReturnNotFoundWhenDeadLetterEventDoesNotExist() throws Exception {

        String token = createUser();

        MvcResult result = mockMvc.perform(
                        get(URL + "/" + generator.nextId())
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isNotFound())
                .andReturn();

        String json = result.getResponse().getContentAsString();

        TypeReference<ResponseHTTP<DeadLetterEventDTO>> typeRef = new TypeReference<>() {};

        ResponseHTTP<DeadLetterEventDTO> response = objectMapper.readValue(json, typeRef);

        assertThat(response.status()).isFalse();
        assertThat(response.data()).isNull();
    }

    @Test
    void shouldFilterDeadLetterEventsByStatus() throws Exception {

        String token =
                createUser();

        createDeadLetterEvent(DeadLetterStatus.PENDING);
        createDeadLetterEvent(DeadLetterStatus.PENDING);
        createDeadLetterEvent(DeadLetterStatus.PROCESSED);

        MvcResult result = mockMvc.perform(
                        get(URL)
                                .param("status", DeadLetterStatus.PENDING.name())
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json =
                result.getResponse().getContentAsString();

        TypeReference<PageResponse<DeadLetterEventDTO>> typeRef =
                new TypeReference<>() {};

        PageResponse<DeadLetterEventDTO> page =
                objectMapper.readValue(json, typeRef);

        assertThat(page.getContent())
                .allMatch(e -> e.getStatus() == DeadLetterStatus.PENDING);
    }

    @Test
    void shouldReturnEmptyPageWhenNoDeadLetterMatchesFilter() throws Exception {

        String token =
                createUser();

        MvcResult result = mockMvc.perform(
                        get(URL)
                                .param("eventType", "UNKNOWN_EVENT")
                                .header("Authorization", "Bearer " + token)
                )
                .andExpect(status().isOk())
                .andReturn();

        String json =
                result.getResponse().getContentAsString();

        TypeReference<PageResponse<DeadLetterEventDTO>> typeRef =
                new TypeReference<>() {};

        PageResponse<DeadLetterEventDTO> page =
                objectMapper.readValue(json, typeRef);

        assertThat(page.getContent()).isEmpty();
    }
}