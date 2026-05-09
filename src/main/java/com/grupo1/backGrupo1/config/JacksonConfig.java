package com.grupo1.backGrupo1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
public class JacksonConfig {

    @Bean
    public ObjectMapper objectMapper() {
        // Use Spring's builder to get sensible defaults; avoids requiring autowiring of Jackson2ObjectMapperBuilder
        return new Jackson2ObjectMapperBuilder().build();
    }
}
