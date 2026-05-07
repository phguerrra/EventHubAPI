package com.grupo1.backGrupo1.model;

import com.grupo1.backGrupo1.model.User;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class UserModelTest {

    @Test
    void gettersAndSettersWork() {
        User u = new User();
        u.setName("João");
        u.setEmail("joao@example.com");
        u.setCpf("12345678901");
        u.setPhone("+559999999");
        u.setAddress("Rua Teste, 123");
        u.setDataNascimento(LocalDate.of(1990, 1, 1));

        assertThat(u.getName()).isEqualTo("João");
        assertThat(u.getEmail()).isEqualTo("joao@example.com");
        assertThat(u.getCpf()).isEqualTo("12345678901");
        assertThat(u.getPhone()).isEqualTo("+559999999");
        assertThat(u.getAddress()).isEqualTo("Rua Teste, 123");
        assertThat(u.getDataNascimento()).isEqualTo(LocalDate.of(1990, 1, 1));
    }

    @Test
    void equalsAndHashCode_consistentForSameFieldValues() {
        User a = new User();
        a.setName("Same");
        a.setEmail("same@example.com");
        a.setCpf("00011122233");

        User b = new User();
        b.setName("Same");
        b.setEmail("same@example.com");
        b.setCpf("00011122233");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
