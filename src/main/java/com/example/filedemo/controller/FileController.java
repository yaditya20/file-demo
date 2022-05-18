package com.example.filedemo.controller;

import com.example.filedemo.payload.UploadFileResponse;
import com.example.filedemo.service.FileStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
public class FileController {

    private static final Logger logger = LoggerFactory.getLogger(FileController.class);

    @Autowired
    private FileStorageService fileStorageService;

    @PostMapping("/uploadFile")
    public UploadFileResponse uploadFile(@RequestParam("file") MultipartFile file, @RequestParam("applicationId") Long applicationId) {
        String fileName = fileStorageService.storeFile(file, applicationId).getDocumentName();

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path(applicationId.toString())
                .path("/downloadFile/")
                .path(fileName)
                .toUriString();

        return new UploadFileResponse(fileName, fileDownloadUri,
                file.getContentType(), file.getSize());
    }

    @PostMapping("/uploadMultipleFiles")
    public List<UploadFileResponse> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files, @RequestParam("applicationId") Long applicationId) {
        return Arrays.asList(files)
                .stream()
                .map(file -> uploadFile(file, applicationId))
                .collect(Collectors.toList());
    }

    @GetMapping("/{applicationId}/downloadFile/{fileName}")
    public ResponseEntity<Resource> downloadFile(@PathVariable("applicationId") Long applicationId, @PathVariable("fileName") String fileName, HttpServletRequest request) {
        // Load file as Resource

        Resource resource = fileStorageService.loadFileAsResource(fileName, applicationId);
        logger.info(resource.toString());
        logger.info(fileName);
        logger.info(resource.getFilename());
        // Try to determine file's content type
        String contentType = null;
        try {
            logger.info(resource.getFile().getAbsolutePath());
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            logger.info(contentType);
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        // Fallback to the default content type if type could not be determined
        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}


