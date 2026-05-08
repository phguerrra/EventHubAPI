package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.service.EventsService;
import com.grupo1.backGrupo1.service.FileStorageService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.nio.file.Paths;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventsControllerUploadTest {

    private static final Path REAL_PHOTO_PATH = Paths.get(
            "C:\\Users\\charl\\OneDrive\\Imagens\\Screenshots\\Captura de tela 2026-05-08 193107.png"
    );

    @TempDir
    Path tempDir;

    @Test
    void shouldCreateEventAndStorePhotoUrl() throws Exception {
        EventsService eventsService = mock(EventsService.class);

        FileStorageService fileStorageService = new FileStorageService();
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());

        when(eventsService.saveEvent(any(Event.class))).thenAnswer(invocation -> {
            Event event = invocation.getArgument(0);
            event.setId(1L);
            event.setDate(null);
            event.setTime(null);
            return event;
        });

        EventsController controller = new EventsController(eventsService, fileStorageService);

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();

        MockHttpSession session = new MockHttpSession();
        session.setAttribute("userId", 1L);
        session.setAttribute("userRole", "ADMIN");

        byte[] photoBytes = Files.readAllBytes(REAL_PHOTO_PATH);
        MockMultipartFile photo = new MockMultipartFile(
                "photo",
                REAL_PHOTO_PATH.getFileName().toString(),
                "image/png",
                photoBytes
        );

        String photoUrl = mockMvc.perform(multipart("/events")
                        .file(photo)
                        .param("title", "Evento com foto")
                        .param("description", "Teste de upload")
                        .param("date", "2026-05-20")
                        .param("time", "20:00:00")
                        .param("location", "Auditório")
                        .param("maxParticipants", "150")
                        .param("majority18", "false")
                        .param("category", "show")
                        .session(session))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.photoUrl").value(org.hamcrest.Matchers.startsWith("/uploads/events/")))
                .andReturn()
                .getResponse()
                .getContentAsString()
                .replaceAll(".*\"photoUrl\":\"([^\"]+)\".*", "$1");

        Path storedFile = tempDir.resolve(photoUrl.replace("/uploads/", "").replace("/", java.io.File.separator));
        assertThat(Files.exists(storedFile)).isTrue();
    }
}
