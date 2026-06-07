package com.grupo1.backGrupo1.exception;

import com.grupo1.backGrupo1.service.email.EmailService;
import io.sentry.Sentry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final EmailService emailService;

    @Value("${app.dev.email:bernardoseveroa@gmail.com}")
    private String devEmail;

    public GlobalExceptionHandler(EmailService emailService) {
        this.emailService = emailService;
    }

    // 401 — credenciais inválidas
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, String>> handleAuthentication(AuthenticationException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(body);
    }

    // 404 — recurso não encontrado
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(EntityNotFoundException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // 400 — regra de negócio violada (ex: email duplicado, evento lotado)
    @ExceptionHandler(BusinessRuleException.class)
    public ResponseEntity<Map<String, String>> handleBusiness(BusinessRuleException ex) {
        Map<String, String> body = new HashMap<>();
        body.put("erro", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 400 — erro de validação dos campos do formulário (@NotBlank, @Email, etc.)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> body = new HashMap<>();
        String msg = ex.getBindingResult().getAllErrors().stream()
                .map(e -> e.getDefaultMessage())
                .findFirst()
                .orElse("Dados inválidos");
        body.put("erro", msg);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // 502 — falha ao enviar email (mensagem genérica, sem vazar detalhes da API)
    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<Map<String, String>> handleEmailSend(EmailSendException ex) {
        log.error("Falha ao enviar email: {}", ex.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("erro", "Não foi possível enviar o email de confirmação. Tente novamente mais tarde.");
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    // 500 — qualquer erro não tratado: loga, manda pro Sentry e notifica o dev
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleOther(Exception ex) {
        log.error("Erro inesperado capturado", ex);

        Sentry.captureException(ex);
        notifyDev(ex);

        Map<String, String> body = new HashMap<>();
        body.put("erro", "Ocorreu um erro inesperado. Nossa equipe foi notificada.");
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private void notifyDev(Exception ex) {
        try {
            String subject = "[ERRO] " + ex.getClass().getSimpleName()
                    + " — " + LocalDateTime.now();

            String stackTrace = Arrays.stream(ex.getStackTrace())
                    .limit(10)
                    .map(StackTraceElement::toString)
                    .collect(Collectors.joining("<br/>"));

            String html = """
                    <h2 style="color:red">Erro inesperado detectado</h2>
                    <p><strong>Tipo:</strong> %s</p>
                    <p><strong>Mensagem:</strong> %s</p>
                    <p><strong>Quando:</strong> %s</p>
                    <h3>Stack Trace (primeiros 10 frames):</h3>
                    <pre>%s</pre>
                    """.formatted(
                    ex.getClass().getName(),
                    ex.getMessage(),
                    LocalDateTime.now(),
                    stackTrace
            );

            emailService.sendConfirmationEmail(devEmail, subject, html);
        } catch (Exception emailEx) {
            log.error("Falha ao enviar email de erro: {}", emailEx.getMessage());
        }
    }
}