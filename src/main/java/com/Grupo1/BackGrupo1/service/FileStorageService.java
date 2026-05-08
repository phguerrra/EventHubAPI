package com.grupo1.backGrupo1.service;

import com.grupo1.backGrupo1.exception.BusinessRuleException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    public String storeEventPhoto(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessRuleException("A foto do evento é obrigatória");
        }

        String originalFilename = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = getExtension(originalFilename);

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessRuleException("Formato de imagem inválido. Use jpg, jpeg, png ou webp");
        }

        try {
            Path uploadPath = Paths.get(uploadDir, "events").toAbsolutePath().normalize();
            Files.createDirectories(uploadPath);

            String storedFilename = UUID.randomUUID() + "." + extension;
            Path targetPath = uploadPath.resolve(storedFilename);

            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, targetPath, StandardCopyOption.REPLACE_EXISTING);
            }

            return "/uploads/events/" + storedFilename;
        } catch (IOException e) {
            throw new BusinessRuleException("Não foi possível salvar a foto do evento");
        }
    }

    private String getExtension(String filename) {
        int lastDot = filename.lastIndexOf('.');
        if (lastDot < 0 || lastDot == filename.length() - 1) {
            throw new BusinessRuleException("Arquivo de imagem sem extensão válida");
        }

        return filename.substring(lastDot + 1).toLowerCase();
    }
}
