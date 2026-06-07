package com.grupo1.backGrupo1.service.email;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.grupo1.backGrupo1.exception.EmailSendException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResendEmailService implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(ResendEmailService.class);

    private final Resend resend;
    private final String from;

    public ResendEmailService(
            @Value("${resend.api.key:}") String apiKey,
            @Value("${resend.from:Eventos <onboarding@resend.dev>}") String from) {

        if (apiKey == null || apiKey.isBlank()) {
            throw new IllegalStateException(
                    "resend.api.key não configurado no application.properties");
        }
        this.resend = new Resend(apiKey);
        this.from = from;
    }

    @Override
    public void sendConfirmationEmail(String to, String subject, String html) {
        try {
            CreateEmailOptions options = CreateEmailOptions.builder()
                    .from(from)
                    .to(to)
                    .subject(subject)
                    .html(html)
                    .build();

            resend.emails().send(options);
            log.info("Email enviado para {} | assunto: {}", to, subject);

        } catch (ResendException e) {
            log.error("Falha ao enviar email para {} | {}", to, e.getMessage());
            throw new EmailSendException("Falha ao enviar email via Resend: " + e.getMessage(), e);
        }
    }
}