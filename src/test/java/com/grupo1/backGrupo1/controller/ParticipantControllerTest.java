package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.service.ParticipantService;
import com.grupo1.backGrupo1.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ParticipantControllerTest {

    private MockMvc mockMvc;

    @Mock
    private ParticipantService service;

    @Mock
    private UserService userService;

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders
                .standaloneSetup(new ParticipantController(service, userService))
                .build();
    }

    @Test
    void cancelWithParticipantIdAsAdminCancelsParticipantById() throws Exception {
        TestingAuthenticationToken admin =
                new TestingAuthenticationToken("admin@gmail.com", null, "ROLE_ADMIN");

        mockMvc.perform(delete("/events/1/participants/cancel")
                        .param("participantId", "6")
                        .principal(admin))
                .andExpect(status().isNoContent());

        verify(service).removeParticipantFromEvent(1L, 6L);
        verify(service, never()).cancelarInscricao("admin@gmail.com", 1L);
    }
}
