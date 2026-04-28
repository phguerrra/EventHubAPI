package com.grupo1.backGrupo1.controller;

import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService service;

    @BeforeEach
    void setup() {
        UserController controller = new UserController(service);
        this.mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void login_user_sets_session_and_returns_response() throws Exception {
        String requestJson = "{\"email\":\"user@example.com\",\"password\":\"secret\"}";

        User user = new User();
        user.setId(1L);
        user.setName("Regular User");
        user.setEmail("user@example.com");
        user.setRole("USER");

        when(service.login(any(LoginDTO.class))).thenReturn(user);
        when(service.isAdmin(user)).thenReturn(false);

        mockMvc.perform(post("/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login realizado com sucesso")))
                .andExpect(content().string(containsString("Regular User")))
                .andExpect(request().sessionAttribute("userId", user.getId()))
                .andExpect(request().sessionAttribute("userRole", user.getRole()))
                .andExpect(request().sessionAttribute("isAdmin", false));
    }

    @Test
    void login_admin_sets_isAdmin_true() throws Exception {
        String requestJson = "{\"email\":\"admin@example.com\",\"password\":\"adminpass\"}";

        User user = new User();
        user.setId(2L);
        user.setName("Admin");
        user.setEmail("admin@example.com");
        user.setRole("ADMIN");

        when(service.login(any(LoginDTO.class))).thenReturn(user);
        when(service.isAdmin(user)).thenReturn(true);

        mockMvc.perform(post("/users/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Login realizado com sucesso")))
                .andExpect(content().string(containsString("Admin")))
                .andExpect(request().sessionAttribute("isAdmin", true));
    }
}
