package com.pharmaorder.fileservice.controller;

import com.pharmaorder.fileservice.service.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/files")
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws IOException {
        String fileKey = fileService.uploadFile(file);
        return ResponseEntity.ok(Map.of(
            "fileKey", fileKey,
            "originalFilename", file.getOriginalFilename() != null ? file.getOriginalFilename() : "unknown"
        ));
    }

    @GetMapping("/{fileKey}")
    public ResponseEntity<InputStreamResource> download(@PathVariable String fileKey) {
        InputStream is = fileService.getFile(fileKey);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileKey + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(new InputStreamResource(is));
    }
}
