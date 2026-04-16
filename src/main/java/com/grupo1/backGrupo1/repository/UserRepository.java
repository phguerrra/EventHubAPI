package com.grupo1.backGrupo1.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.grupo1.backGrupo1.model.User;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    Optional<User> findByCpf(String cpf);

    boolean existsByCpf(String cpf);
}