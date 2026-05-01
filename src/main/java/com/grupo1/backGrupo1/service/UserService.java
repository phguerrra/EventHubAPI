package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.security.JwtService;
import com.grupo1.backGrupo1.util.CpfValidator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final JwtService jwtService;

    public UserService(UserRepository repo, PasswordEncoder encoder, JwtService jwtService) {
        this.repo = repo;
        this.encoder = encoder;
        this.jwtService = jwtService;
    }

    // ==============================
    // REGISTER
    // ==============================
    public User register(UserDTO dto) {

        validateUser(dto);

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword()));
        user.setCpf(dto.getCpf());
        user.setDataNascimento(dto.getDataNascimento());
        user.setRole("USER");

        return repo.save(user);
    }

    // ==============================
    // LOGIN (AUTENTICA E RETORNA O USUÁRIO)
    // ==============================
    public User login(LoginDTO dto) {

        validateLogin(dto);

        User user = repo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new RuntimeException("Email ou senha inválidos"));

        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new RuntimeException("Email ou senha inválidos");
        }

        return user;
    }

    public User findByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }

    private void validateUser(UserDTO dto) {

        if (repo.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email já cadastrado");
        }

        if (repo.existsByCpf(dto.getCpf())) {
            throw new RuntimeException("CPF já cadastrado");
        }

        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new RuntimeException("CPF é obrigatório");
        }

        if (!CpfValidator.isValid(dto.getCpf())) {
            throw new RuntimeException("CPF inválido");
        }

        if (dto.getDataNascimento() == null) {
            throw new RuntimeException("Data de nascimento é obrigatória");
        }

        if (dto.getDataNascimento().isAfter(LocalDate.now())) {
            throw new RuntimeException("Data de nascimento inválida");
        }
    }

    private void validateLogin(LoginDTO dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new RuntimeException("Email é obrigatório");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new RuntimeException("Senha é obrigatória");
        }
    }


    public User findById(Long userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
    }

    public boolean isMaiorDeIdade(User user) {
        return Period.between(user.getDataNascimento(), LocalDate.now()).getYears() >= 18;
    }

    public boolean isMaiorDeIdadeById(Long userId) {
        return isMaiorDeIdade(findById(userId));
    }

    public int calcularIdade(User user) {
        return Period.between(user.getDataNascimento(), LocalDate.now()).getYears();
    }

    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
}