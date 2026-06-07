package com.grupo1.backGrupo1.integration;

import com.grupo1.backGrupo1.model.Notification;
import com.grupo1.backGrupo1.repository.NotificationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class NotificationIntegrationTest {

    @Autowired MockMvc mockMvc;
    @Autowired NotificationRepository notificationRepository;

    @AfterEach
    void cleanup() {
        notificationRepository.deleteAll();
    }

    @Test
    @DisplayName("GET /avisos deve retornar lista vazia quando não há avisos")
    void listAll_empty() throws Exception {
        mockMvc.perform(get("/avisos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(0));
    }

    @Test
    @DisplayName("POST /avisos sem autenticação deve retornar 403 ou 401")
    void create_unauthenticated_returns401or403() throws Exception {
        mockMvc.perform(post("/avisos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                    {
                        "titulo": "Teste",
                        "conteudo": "Conteúdo",
                        "type": "GENERAL"
                    }
                """))
                .andExpect(status().is4xxClientError());
    }

    @Test
    @DisplayName("GET /avisos deve retornar avisos não deletados")
    void listAll_returnsNotDeleted() throws Exception {
        Notification n = new Notification();
        n.setTitulo("Aviso Ativo");
        n.setConteudo("Conteúdo");
        n.setTipo(Notification.Type.GENERAL);
        notificationRepository.save(n);

        Notification deleted = new Notification();
        deleted.setTitulo("Aviso Deletado");
        deleted.setConteudo("Conteúdo");
        deleted.setTipo(Notification.Type.GENERAL);
        deleted.setDeleted(true);
        notificationRepository.save(deleted);

        mockMvc.perform(get("/avisos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].titulo").value("Aviso Ativo"));
    }
}