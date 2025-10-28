package com.empresa.portal.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.annotation.PostConstruct;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${file.upload-dir.video:./uploads/videos}")
    private String videoUploadDir;

    @Value("${file.upload-dir.pdf:./uploads/pdfs}")
    private String pdfUploadDir;

    private Path videoStorageLocation;
    private Path pdfStorageLocation;

    @PostConstruct
    public void init() {
        try {
            this.videoStorageLocation = Paths.get(videoUploadDir).toAbsolutePath().normalize();
            this.pdfStorageLocation = Paths.get(pdfUploadDir).toAbsolutePath().normalize();

            Files.createDirectories(this.videoStorageLocation);
            Files.createDirectories(this.pdfStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("No se pudieron crear los directorios para almacenar archivos", ex);
        }
    }

    public String storeVideoFile(MultipartFile file) {
        return storeFile(file, videoStorageLocation, "video");
    }

    public String storePdfFile(MultipartFile file) {
        return storeFile(file, pdfStorageLocation, "pdf");
    }

    private String storeFile(MultipartFile file, Path storageLocation, String fileType) {
        try {
            if (file.isEmpty()) {
                throw new RuntimeException("Archivo vacío: " + file.getOriginalFilename());
            }

            // nombre unico
            String originalFileName = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String fileName = UUID.randomUUID().toString() + fileExtension;

            // Copiar archivo
            Path targetLocation = storageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return fileName;
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo almacenar el archivo " + file.getOriginalFilename(), ex);
        }
    }

    public Resource loadVideoFile(String fileName) {
        try {
            Path filePath = videoStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo de video no encontrado: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando archivo de video: " + fileName, e);
        }
    }

    public Resource loadPdfFile(String fileName) {
        try {
            Path filePath = pdfStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                throw new RuntimeException("Archivo PDF no encontrado: " + fileName);
            }
        } catch (Exception e) {
            throw new RuntimeException("Error cargando archivo PDF: " + fileName, e);
        }
    }

    public void deleteFile(String fileName, String fileType) {
        try {
            Path storageLocation = fileType.equals("video") ? videoStorageLocation : pdfStorageLocation;
            Path filePath = storageLocation.resolve(fileName).normalize();

            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new RuntimeException("No se pudo eliminar el archivo: " + fileName, ex);
        }
    }
}