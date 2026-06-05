package com.grupo1.backGrupo1.service.email;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.grupo1.backGrupo1.exception.EmailSendException;
import com.grupo1.backGrupo1.service.email.dto.ResendEmailRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;

@Service
public class ResendEmailService implements EmailService {

    private final String apiKey;
    private final String from;
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ResendEmailService(@Value("${resend.api.key:}") String apiKey,
                              @Value("${resend.from:Eventos <onboarding@resend.dev>}") String from,
                              ObjectMapper objectMapper) {
        this.apiKey = apiKey;
        this.from = from;
        this.objectMapper = objectMapper;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
    }

    @Override
    public void sendConfirmationEmail(String to, String subject, String html) {
        if (apiKey == null || apiKey.isBlank()) {
            throw new EmailSendException("Resend API key is not configured (resend.api.key)");
        }

        try {
            ResendEmailRequest payload = new ResendEmailRequest(from, List.of(to), subject, html);
            String body = objectMapper.writeValueAsString(payload);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://api.resend.com/emails"))
                    .header("Authorization", "Bearer " + apiKey)
                    .header("Content-Type", "application/json")
                    .timeout(Duration.ofSeconds(20))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                String msg = String.format("Resend API returned status=%d body=%s", response.statusCode(), response.body());
                throw new EmailSendException(msg);
            }

        } catch (EmailSendException e) {
            throw e;
        } catch (Exception e) {
            throw new EmailSendException("Failed to send email via Resend", e);
        }
    }
}
