package com.grupo1.backGrupo1.service;

import org.springframework.stereotype.Service;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.dto.UserDTO;
import com.grupo1.backGrupo1.exception.EntityNotFoundException;
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

        // usuario criado pelo cadastro normal sera um usuario comum
        u.setRole("USER");

        return repo.save(u);
    }

    public User findById(Long userId) {
        return repo.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado com id: " + userId));
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