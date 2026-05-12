package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.dto.UserResponseDTO;
import com.grupo1.backGrupo1.exception.AuthenticationException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.security.JwtService;
import com.grupo1.backGrupo1.util.CpfValidator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

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

        try {
            logger.info("Iniciando registro de usuário: {}", dto.getEmail());

            validateUser(dto);

            User user = new User();
            user.setName(dto.getName());
            user.setEmail(dto.getEmail());
            user.setPassword(encoder.encode(dto.getPassword()));
            user.setCpf(dto.getCpf());
            user.setDataNascimento(dto.getDataNascimento());
            user.setRole("USER");
            user.setPhone(dto.getPhone());
            user.setAddress(dto.getAddress());

            User savedUser = repo.save(user);

            logger.info("Usuário registrado com sucesso: {}", savedUser.getEmail());

            return savedUser;

        } catch (Exception e) {
            logger.error("Erro ao registrar usuário: {}", e.getMessage(), e);
            throw e;
        }
    }

    // ==============================
    // LOGIN
    // ==============================
    public User login(LoginDTO dto) {

        try {
            logger.info("Tentativa de login: {}", dto.getEmail());

            validateLogin(dto);

            User user = repo.findByEmail(dto.getEmail())
                    .orElseThrow(() -> {
                        logger.warn("Login falhou - email não encontrado: {}", dto.getEmail());
                        return new AuthenticationException("Email ou senha inválidos");
                    });

            if (!encoder.matches(dto.getPassword(), user.getPassword())) {
                logger.warn("Login falhou - senha inválida para: {}", dto.getEmail());
                throw new AuthenticationException("Email ou senha inválidos");
            }

            logger.info("Login realizado com sucesso: {}", dto.getEmail());

            return user;

        } catch (Exception e) {
            logger.error("Erro no login: {}", e.getMessage(), e);
            throw e;
        }
    }

    public User findByEmail(String email) {
        logger.info("Buscando usuário por email: {}", email);

        return repo.findByEmail(email)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com email: {}", email);
                    return new RuntimeException("Usuário não encontrado");
                });
    }

    public UserResponseDTO toResponse(User user) {
        if (user == null) return null;

        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getCpf(),
                user.getDataNascimento(),
                user.getRole(),
                user.getPhone(),
                user.getAddress()
        );
    }

    private void validateUser(UserDTO dto) {

        if (repo.existsByEmail(dto.getEmail())) {
            logger.warn("Tentativa de cadastro com email já existente: {}", dto.getEmail());
            throw new RuntimeException("Email já cadastrado");
        }

        if (repo.existsByCpf(dto.getCpf())) {
            logger.warn("Tentativa de cadastro com CPF já existente: {}", dto.getCpf());
            throw new RuntimeException("CPF já cadastrado");
        }

        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            logger.warn("CPF não informado");
            throw new RuntimeException("CPF é obrigatório");
        }

        if (!CpfValidator.isValid(dto.getCpf())) {
            logger.warn("CPF inválido: {}", dto.getCpf());
            throw new RuntimeException("CPF inválido");
        }

        if (dto.getDataNascimento() == null) {
            logger.warn("Data de nascimento não informada");
            throw new RuntimeException("Data de nascimento é obrigatória");
        }

        if (dto.getDataNascimento().isAfter(LocalDate.now())) {
            logger.warn("Data de nascimento inválida: {}", dto.getDataNascimento());
            throw new RuntimeException("Data de nascimento inválida");
        }
    }

    private void validateLogin(LoginDTO dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            logger.warn("Login com email vazio");
            throw new RuntimeException("Email é obrigatório");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            logger.warn("Login com senha vazia");
            throw new RuntimeException("Senha é obrigatória");
        }
    }

    public User findById(Long userId) {
        logger.info("Buscando usuário por ID: {}", userId);

        return repo.findById(userId)
                .orElseThrow(() -> {
                    logger.warn("Usuário não encontrado com id: {}", userId);
                    return new EntityNotFoundException("Usuário não encontrado com id: " + userId);
                });
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