package com.grupo1.backGrupo1.service.email;

public interface EmailService {

    void sendConfirmationEmail(String to, String subject, String html);
}
