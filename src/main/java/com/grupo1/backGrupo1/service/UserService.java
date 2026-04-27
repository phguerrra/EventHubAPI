package com.grupo1.backGrupo1.service;

import org.springframework.stereotype.Service;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.exception.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    public UserService(UserRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    public User register(UserDTO dto) {

        if (repo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (repo.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (dto.getDataNascimento() == null) {
            throw new RuntimeException("Data de nascimento é obrigatória");
        }

        if (dto.getDataNascimento().isAfter(LocalDate.now())) {
            throw new RuntimeException("Data de nascimento inválida");
        }

        User u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        u.setPassword(encoder.encode(dto.getPassword()));
        u.setCpf(dto.getCpf());
        u.setDataNascimento(dto.getDataNascimento());
        u.setRole("USER");

        return repo.save(u);
    }

    public User login(LoginDTO dto) {
        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new AuthenticationException("Email é obrigatório");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new AuthenticationException("Senha é obrigatória");
        }

        User user = repo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Email ou senha inválidos"));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Email ou senha inválidos");
        }

        return user;
    }

    public User findById(Long userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
    }

    public boolean isAdmin(Long userId) {
        User user = findById(userId);
        return isAdmin(user);
    }

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }

    public boolean isUser(User user) {
        return user != null && "USER".equals(user.getRole());
    }

    public boolean isMaiorDeIdade(User user) {
        return Period.between(user.getDataNascimento(), LocalDate.now()).getYears() >= 18;
    }

    public boolean isMaiorDeIdadeById(Long userId) {
        User user = repo.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        return isMaiorDeIdade(user);
    }

    public int calcularIdade(User user) {
        return Period.between(user.getDataNascimento(), LocalDate.now()).getYears();
    }
}