package com.grupo1.backGrupo1.config;

import com.grupo1.backGrupo1.model.Event;
import com.grupo1.backGrupo1.model.Participant;
import com.grupo1.backGrupo1.model.User;
import com.grupo1.backGrupo1.repository.EventsRepository;
import com.grupo1.backGrupo1.repository.ParticipantRepository;
import com.grupo1.backGrupo1.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            UserRepository userRepository,
            EventsRepository eventsRepository,
            ParticipantRepository participantRepository,
            PasswordEncoder passwordEncoder
    ) {
        return args -> {

            if (userRepository.count() > 0 || eventsRepository.count() > 0) {
                System.out.println("Seed ignorada: banco de dados ja possui dados");
                return;
            }

            User admin = new User();
            admin.setName("Administrador");
            admin.setEmail("admin@gmail.com");
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setCpf("477.925.800-67");
            admin.setRole("ADMIN");
            admin.setDataNascimento(LocalDate.of(1990, 1, 1));

            User user = new User();
            user.setName("João Silva");
            user.setEmail("joao@gmail.com");
            user.setPassword(passwordEncoder.encode("123456"));
            user.setCpf("046.251.650-40");
            user.setRole("USER");
            user.setDataNascimento(LocalDate.of(1995, 6, 15));

            User user2 = new User();
            user2.setName("Gabriel Paes");
            user2.setEmail("gabriel@gmail.com");
            user2.setPassword(passwordEncoder.encode("123456"));
            user2.setCpf("474.629.770-36");
            user2.setRole("USER");
            user2.setDataNascimento(LocalDate.of(1998, 3, 22));

            User user3 = new User();
            user3.setName("Carlos Martins");
            user3.setEmail("carlos@gmail.com");
            user3.setPassword(passwordEncoder.encode("123456"));
            user3.setCpf("401.407.090-53");
            user3.setRole("USER");
            user3.setDataNascimento(LocalDate.of(2000, 9, 10));

            userRepository.save(admin);
            userRepository.save(user);
            userRepository.save(user2);
            userRepository.save(user3);

            Event event1 = new Event();
            event1.setTitle("Eres");
            event1.setDescription("Escola Regional de Engenharia de Software");
            event1.setDate(LocalDate.of(2026, 8, 10));
            event1.setTime(LocalTime.of(19, 0));
            event1.setLocation("Auditório principal");
            event1.setMaxParticipants(100);
            event1.setMajority18(false);
            event1.setCategory("Tecnologia");

            Event event2 = new Event();
            event2.setTitle("Campeonato de Futebol");
            event2.setDescription("Evento esportivo com times regionais");
            event2.setDate(LocalDate.of(2026, 9, 15));
            event2.setTime(LocalTime.of(14, 0));
            event2.setLocation("Arena Municipal");
            event2.setMaxParticipants(300);
            event2.setMajority18(false);
            event2.setCategory("Esporte");

            eventsRepository.save(event1);
            eventsRepository.save(event2);

            Participant participant = new Participant();
            participant.setName(user.getName());
            participant.setEmail(user.getEmail());
            participant.setCpf(user.getCpf());
            participant.setPhone("55999468981");
            participant.setEvent(event1);

            Participant participant2 = new Participant();
            participant2.setName(user2.getName());
            participant2.setEmail(user2.getEmail());
            participant2.setCpf(user2.getCpf());
            participant2.setPhone("55996387923");
            participant2.setEvent(event1);

            participantRepository.save(participant);
            participantRepository.save(participant2);

            System.out.println("Seed executada com sucesso!");
        };
    }
}