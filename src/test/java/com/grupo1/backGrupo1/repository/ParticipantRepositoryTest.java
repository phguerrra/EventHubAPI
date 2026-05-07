package com.grupo1.backGrupo1.model;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ParticipantModelTest {

    @Test
    void gettersAndSettersAndDeletedFlag() {
        Event e = new Event();
        e.setTitle("Evento Unit");
        e.setMaxParticipants(20);

        Participant p = new Participant();
        p.setName("Carlos");
        p.setEmail("carlos@example.com");
        p.setCpf("99988877766");
        p.setPhone("+551199999");
        p.setEvent(e);

        assertThat(p.getName()).isEqualTo("Carlos");
        assertThat(p.getEmail()).isEqualTo("carlos@example.com");
        assertThat(p.getCpf()).isEqualTo("99988877766");
        assertThat(p.getPhone()).isEqualTo("+551199999");
        assertThat(p.getEvent()).isEqualTo(e);

        p.setDeleted(true);
        assertThat(p.isDeleted()).isTrue();
    }

    @Test
    void equalsAndHashCode_consistent() {
        Participant a = new Participant();
        a.setName("A");
        a.setEmail("a@example.com");

        Participant b = new Participant();
        b.setName("A");
        b.setEmail("a@example.com");

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
