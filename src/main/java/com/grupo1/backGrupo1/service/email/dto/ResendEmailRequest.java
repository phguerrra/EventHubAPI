package com.grupo1.backGrupo1.service.email.dto;

import java.util.List;

public record ResendEmailRequest(String from, List<String> to, String subject, String html) {}
