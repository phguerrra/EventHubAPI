package com.grupo1.backGrupo1.service;

import org.springframework.stereotype.Service;
import com.grupo1.backGrupo1.repository.UserRepository;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.dto.UserDTO;
import org.springframework.security.crypto.password.PasswordEncoder;

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

        User u = new User();
        u.setName(dto.getName());
        u.setEmail(dto.getEmail());
        u.setPassword(encoder.encode(dto.getPassword()));

        return repo.save(u);
    }
}