package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.dto.LoginDTO;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.dto.UserProfileUpdateDTO;
import com.grupo1.backGrupo1.dto.UserResponseDTO;
import com.grupo1.backGrupo1.exception.AuthenticationException;
import com.grupo1.backGrupo1.exception.BusinessRuleException;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.security.JwtService;
import com.grupo1.backGrupo1.util.CpfValidator;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

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
    // CADASTRAR NOVO USUÁRIO
    // ==============================
    public User register(UserDTO dto) {
        // Valida os dados antes de salvar
        validateUser(dto);

        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setPassword(encoder.encode(dto.getPassword())); // senha sempre criptografada
        user.setCpf(dto.getCpf());
        user.setDataNascimento(dto.getDataNascimento());
        user.setRole("USER"); // todo cadastro começa como USER
        user.setPhone(dto.getPhone());
        user.setAddress(dto.getAddress());

        return repo.save(user);
    }

    // ==============================
    // LOGIN — valida credenciais e retorna o usuário
    // ==============================
    public User login(LoginDTO dto) {
        validateLogin(dto);

        User user = repo.findByEmail(dto.getEmail())
                .orElseThrow(() -> new AuthenticationException("Email ou senha inválidos"));

        // Compara a senha enviada com o hash salvo no banco
        if (!encoder.matches(dto.getPassword(), user.getPassword())) {
            throw new AuthenticationException("Email ou senha inválidos");
        }

        return user;
    }

    // Busca usuário pelo e-mail (usado internamente em vários lugares)
    public User findByEmail(String email) {
        return repo.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));
    }

    // Busca usuário pelo ID
    public User findById(Long userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
    }

    // Converte entidade User para DTO de resposta (sem expor senha)
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

    // ==============================
    // LISTAR TODOS OS USUÁRIOS (apenas admin)
    // ==============================
    public List<UserResponseDTO> listAllUsers() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    // ==============================
    // ALTERAR PAPEL DO USUÁRIO (USER ↔ ADMIN)
    // ==============================
    public UserResponseDTO updateRole(Long userId, String role) {
        String normalizedRole = role == null ? "" : role.trim().toUpperCase();

        if (!normalizedRole.equals("ADMIN") && !normalizedRole.equals("USER")) {
            throw new BusinessRuleException("Papel inválido. Use ADMIN ou USER");
        }

        User user = findById(userId);
        user.setRole(normalizedRole);
        return toResponse(repo.save(user));
    }

    // ==============================
    // ATUALIZAR PERFIL DO USUÁRIO LOGADO
    // ==============================
    public UserResponseDTO updateProfile(String email, UserProfileUpdateDTO dto) {
        User user = findByEmail(email);

        String name = dto.getName() == null ? "" : dto.getName().trim();
        if (name.isBlank()) {
            throw new BusinessRuleException("Nome é obrigatório");
        }

        if (dto.getDataNascimento() != null && dto.getDataNascimento().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Data de nascimento inválida");
        }

        user.setName(name);
        user.setPhone(normalizeOptional(dto.getPhone()));
        user.setAddress(normalizeOptional(dto.getAddress()));
        user.setDataNascimento(dto.getDataNascimento());

        return toResponse(repo.save(user));
    }

    // ==============================
    // HELPERS
    // ==============================

    // Remove espaços e retorna null se o campo vier vazio
    private String normalizeOptional(String value) {
        if (value == null) return null;
        String trimmed = value.trim();
        return trimmed.isBlank() ? null : trimmed;
    }

    // Validações do cadastro — lança 400 em vez de 500 para erros esperados
    private void validateUser(UserDTO dto) {

        // Valida formato/presença antes de consultar o banco
        if (dto.getCpf() == null || dto.getCpf().isBlank()) {
            throw new BusinessRuleException("CPF é obrigatório");
        }

        if (!CpfValidator.isValid(dto.getCpf())) {
            throw new BusinessRuleException("CPF inválido");
        }

        if (dto.getDataNascimento() == null) {
            throw new BusinessRuleException("Data de nascimento é obrigatória");
        }

        if (dto.getDataNascimento().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("Data de nascimento inválida");
        }

        // Verifica unicidade no banco só depois que os dados básicos estão ok
        if (repo.existsByEmail(dto.getEmail())) {
            throw new BusinessRuleException("Email já cadastrado");
        }

        if (repo.existsByCpf(dto.getCpf())) {
            throw new BusinessRuleException("CPF já cadastrado");
        }
    }

    // Validações básicas do login
    private void validateLogin(LoginDTO dto) {

        if (dto.getEmail() == null || dto.getEmail().isBlank()) {
            throw new BusinessRuleException("Email é obrigatório");
        }

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessRuleException("Senha é obrigatória");
        }
    }

    // Verifica se o usuário tem 18 anos ou mais
    public boolean isMaiorDeIdade(User user) {
        return Period.between(user.getDataNascimento(), LocalDate.now()).getYears() >= 18;
    }

    public boolean isMaiorDeIdadeById(Long userId) {
        return isMaiorDeIdade(findById(userId));
    }

    // Retorna a idade calculada do usuário
    public int calcularIdade(User user) {
        return Period.between(user.getDataNascimento(), LocalDate.now()).getYears();
    }

    // Checa se o usuário tem papel de administrador
    public boolean isAdmin(User user) {
        return user != null && "ADMIN".equals(user.getRole());
    }
}