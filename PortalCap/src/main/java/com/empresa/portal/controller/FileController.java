package com.empresa.portal.controller;

import com.empresa.portal.service.FileStorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private FileStorageService fileStorageService;

    @GetMapping("/video/{fileName:.+}")
    public ResponseEntity<Resource> serveVideoFile(@PathVariable String fileName) {
        try {
            Resource videoFile = fileStorageService.loadVideoFile(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType("video/mp4"))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + videoFile.getFilename() + "\"")
                    .body(videoFile);

        } catch (Exception e) {
            System.out.println("Error cargando video: " + fileName + " - " + e.getMessage());

            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION,
                            "https://commondatastorage.googleapis.com/gtv-videos-bucket/sample/BigBuckBunny.mp4")
                    .build();
        }
    }

    @GetMapping("/pdf/{fileName:.+}")
    public ResponseEntity<Resource> servePdfFile(@PathVariable String fileName) {
        try {
            Resource pdfFile = fileStorageService.loadPdfFile(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_PDF)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + pdfFile.getFilename() + "\"")
                    .body(pdfFile);

        } catch (Exception e) {
            System.out.println("Error cargando PDF: " + fileName + " - " + e.getMessage());

            // pdf de prueba
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header(HttpHeaders.LOCATION,
                            "https://www.w3.org/WAI/ER/tests/xhtml/testfiles/resources/pdf/dummy.pdf")
                    .build();
        }
    }
}