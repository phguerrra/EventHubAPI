package com.grupo1.backGrupo1.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.grupo1.backGrupo1.exception.InvalidTokenException;
import com.grupo1.backGrupo1.exception.TokenExpiredException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.LinkedHashMap;

@Service
public class TicketJwtService {

    @Value("${tickets.jwt.secret:change_this_secret_to_env}")
    private String secret;

    // Generates a simple HS256 JWT (header.payload.signature)
    public String generateToken(String ticketId, Long eventId, long expirationMillis) {
        long now = Instant.now().getEpochSecond();
        long exp = now + (expirationMillis / 1000);

        String header = toJson(Map.of("alg", "HS256", "typ", "JWT"));
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("sub", ticketId);
        payload.put("eventId", eventId);
        payload.put("iat", now);
        payload.put("exp", exp);

        String headerB64 = base64UrlEncode(header.getBytes(StandardCharsets.UTF_8));
        String payloadB64 = base64UrlEncode(toJson(payload).getBytes(StandardCharsets.UTF_8));
        String signingInput = headerB64 + "." + payloadB64;
        String signature = signHmacSha256(signingInput, secret);
        return signingInput + "." + signature;
    }

    // Validates signature and expiration, returns payload as map
    public Map<String, Object> validateTokenAndGetPayload(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) throw new InvalidTokenException("Token inválido");
            String headerB64 = parts[0];
            String payloadB64 = parts[1];
            String sig = parts[2];

            String signingInput = headerB64 + "." + payloadB64;
            String expectedSig = signHmacSha256(signingInput, secret);
            if (!constantTimeEquals(sig, expectedSig)) {
                throw new InvalidTokenException("Assinatura inválida");
            }

            String payloadJson = new String(Base64.getUrlDecoder().decode(payloadB64), StandardCharsets.UTF_8);
            java.util.Map<String, Object> payload = new java.util.HashMap<>();
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            payload = mapper.readValue(payloadJson, java.util.Map.class);

            Object expObj = payload.get("exp");
            if (expObj != null) {
                long exp = ((Number) expObj).longValue();
                long now = Instant.now().getEpochSecond();
                if (now > exp) throw new TokenExpiredException("Token expirado", null);
            }

            return payload;
        } catch (InvalidTokenException | TokenExpiredException e) {
            throw e;
        } catch (Exception e) {
            throw new InvalidTokenException("Token inválido", e);
        }
    }

    private String toJson(Object obj) {
        try {
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            return mapper.writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private String base64UrlEncode(byte[] bytes) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    private String signHmacSha256(String data, String key) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] sig = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(sig);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao assinar token", e);
        }
    }

    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) return false;
        byte[] aBytes = a.getBytes(StandardCharsets.UTF_8);
        byte[] bBytes = b.getBytes(StandardCharsets.UTF_8);
        int result = 0;
        for (int i = 0; i < aBytes.length; i++) result |= aBytes[i] ^ bBytes[i];
        return result == 0;
    }
}
