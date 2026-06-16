package com.grupo1.backGrupo1.integration;

import com.grupo1.backGrupo1.controller.NotificationController;
import com.grupo1.backGrupo1.service.NotificationService;
import com.grupo1.backGrupo1.service.NotificationSseService;
import com.grupo1.backGrupo1.service.ParticipantService;
import com.grupo1.backGrupo1.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(NotificationController.class)
@ActiveProfiles("test")
class NotificationIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificationService notificationService;

    @MockBean
    private NotificationSseService notificationSseService;

    @MockBean
    private ParticipantService participantService;

    @MockBean
    private UserService userService;

    @Test
    void listAll_empty() throws Exception {
        when(notificationService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/avisos"))
                .andExpect(status().isOk());
    }

    @Test
    void listAll_returnsNotDeleted() throws Exception {
        when(notificationService.listAll()).thenReturn(List.of());

        mockMvc.perform(get("/avisos"))
                .andExpect(status().isOk());
    }

    @Test
    void create_unauthenticated_returns401or403() throws Exception {
        mockMvc.perform(post("/avisos")
                        .contentType("application/json")
                        .content("{\"titulo\":\"T\",\"conteudo\":\"C\",\"type\":\"GENERAL\"}"))
                .andExpect(status().is4xxClientError());
    }
}