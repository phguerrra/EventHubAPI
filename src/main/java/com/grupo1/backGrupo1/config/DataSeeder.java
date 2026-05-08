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

import java.time.LocalDate;
import java.time.LocalTime;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seedDatabase(
            UserRepository userRepository,
            EventsRepository eventsRepository,
            ParticipantRepository participantRepository
    )
    {
        return args -> {

            if(userRepository.count() > 0 || eventsRepository.count() > 0){
                System.out.println("Seed ignorada: banco de dados ja possui dados");
                return;
            }

            User admin = new User();

            admin.setName("Administrador");
            admin.setEmail("admin@gmail.com");
            admin.setPassword("123456");
            admin.setCpf("477.925.800-67");
            admin.setRole("ADMIN");

            User user = new User();

            user.setName("João Silva");
            user.setEmail("joao@gmail.com");
            user.setPassword("123456");
            user.setCpf("734.915.240-08");
            user.setRole("USER");

            userRepository.save(admin);
            userRepository.save(user);

            Event event1 = new Event();

            event1.setTitle("Eres");
            event1.setDescription("Escola Regional de Engenharia de Software");
            event1.setDate(LocalDate.of(2026, 5, 10));
            event1.setTime(LocalTime.of(19, 0));
            event1.setLocation("Auditório principal");
            event1.setMaxParticipants(100);
            event1.setMajority18(false);
            event1.setCategory("Tecnologia");


            Event event2 = new Event();
            event2.setTitle("Campeonato de Futebol");
            event2.setDescription("Evento esportivo com times regionais");
            event2.setDate(LocalDate.of(2026, 6, 15));
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

            participantRepository.save(participant);

            System.out.println("Seed executada com sucesso!");

        };
    }
}
