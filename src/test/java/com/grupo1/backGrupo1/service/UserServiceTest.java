package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.exception.AuthenticationException;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository repo;

    @Mock
    private PasswordEncoder encoder;

    @InjectMocks
    private UserService service;

    @BeforeEach
    void setup() {
        // Mockito já inicializa tudo
    }

    // =========================
    // LOGIN USER
    // =========================
    @Test
    void login_user_success() {

        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("secret");

        User user = new User();
        user.setId(1L);
        user.setEmail(dto.getEmail());
        user.setPassword("encoded-pass");
        user.setRole("USER");
        user.setName("Regular User");
        user.setDataNascimento(LocalDate.of(1990,1,1));

        when(repo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

        User result = service.login(dto);

        assertNotNull(result);
        assertEquals("USER", result.getRole());
        assertFalse(service.isAdmin(result));

        // ✔ verifica chamadas
        verify(repo).findByEmail(dto.getEmail());
        verify(encoder).matches(dto.getPassword(), user.getPassword());
    }

    // =========================
    // LOGIN ADMIN
    // =========================
    @Test
    void login_admin_success() {

        LoginDTO dto = new LoginDTO();
        dto.setEmail("admin@example.com");
        dto.setPassword("adminpass");

        User user = new User();
        user.setId(2L);
        user.setEmail(dto.getEmail());
        user.setPassword("encoded-admin");
        user.setRole("ADMIN");
        user.setName("Administrator");
        user.setDataNascimento(LocalDate.of(1985,1,1));

        when(repo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(dto.getPassword(), user.getPassword())).thenReturn(true);

        User result = service.login(dto);

        assertNotNull(result);
        assertEquals("ADMIN", result.getRole());
        assertTrue(service.isAdmin(result));

        verify(repo).findByEmail(dto.getEmail());
        verify(encoder).matches(dto.getPassword(), user.getPassword());
    }

    // =========================
    // EMAIL NÃO EXISTE
    // =========================
    @Test
    void login_nonexistent_email_throws() {

        LoginDTO dto = new LoginDTO();
        dto.setEmail("noone@example.com");
        dto.setPassword("x");

        when(repo.findByEmail(dto.getEmail())).thenReturn(Optional.empty());

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> service.login(dto)
        );

        assertNotNull(ex);

        verify(repo).findByEmail(dto.getEmail());
        verifyNoInteractions(encoder);
    }

    // =========================
    // SENHA ERRADA
    // =========================
    @Test
    void login_wrong_password_throws() {

        LoginDTO dto = new LoginDTO();
        dto.setEmail("user@example.com");
        dto.setPassword("wrong");

        User user = new User();
        user.setId(3L);
        user.setEmail(dto.getEmail());
        user.setPassword("encoded-pass");
        user.setRole("USER");
        user.setDataNascimento(LocalDate.of(1990,1,1));

        when(repo.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));
        when(encoder.matches(dto.getPassword(), user.getPassword())).thenReturn(false);

        AuthenticationException ex = assertThrows(
                AuthenticationException.class,
                () -> service.login(dto)
        );

        assertNotNull(ex);

        verify(repo).findByEmail(dto.getEmail());
        verify(encoder).matches(dto.getPassword(), user.getPassword());
    }

    // =========================
    // TESTE ISOLADO isAdmin
    // =========================
    @Test
    void isAdmin_deve_retornar_true_para_admin() {
        User user = new User();
        user.setRole("ADMIN");

        assertTrue(service.isAdmin(user));
    }

    @Test
    void isAdmin_deve_retornar_false_para_user() {
        User user = new User();
        user.setRole("USER");

        assertFalse(service.isAdmin(user));
    }
}