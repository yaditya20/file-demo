package com.example.filedemo.service;

import com.example.filedemo.controller.FileController;
import com.example.filedemo.exception.FileStorageException;
import com.example.filedemo.exception.MyFileNotFoundException;
import com.example.filedemo.model.Document;
import com.example.filedemo.property.FileStorageProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    private final Path fileStorageLocation;

    private final DocumentService documentService;
//    @Autowired
//    private FileCheckSum fileCheckSum;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties, DocumentService documentService) {
        this.documentService = documentService;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Document storeFile(MultipartFile file, Long applicationId) {
        // Normalize file name
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            /*// Get MD5 Checksum
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            logger.info("here just before md5");
            String md5CheckSum = FileCheckSum.getFileChecksum(messageDigest, file.getResource().getFile());
            logger.info(md5CheckSum);
            // File rename to MD5 CheckSum value
            File fileName1 = file.getResource().getFile();
            logger.info(String.valueOf(fileName1));
            boolean rename = fileName1.renameTo(new File(md5CheckSum));
            logger.info(String.valueOf(rename));
            if (rename) {
                System.out.println(fileName1.getName());
                fileName = StringUtils.cleanPath(fileName1.getName());
                logger.info(fileName);
            } else
                fileName = StringUtils.cleanPath(file.getOriginalFilename());*/

            // Check if the file's name contains invalid characters
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            String fileStorageLocationPerApplicationId =
                    this.fileStorageLocation + File.separator + applicationId;

            Path fileStoragePathPerApplicationId = Paths
                    .get(fileStorageLocationPerApplicationId)
                    .toAbsolutePath()
                    .normalize();
            if (!Files.exists(fileStoragePathPerApplicationId))
                Files.createDirectory(fileStoragePathPerApplicationId);
            // Copy file to the target location (Replacing existing file with the same name)
            // Path targetLocation = this.fileStorageLocation.resolve(fileName); --> Original
            Path targetLocation = fileStoragePathPerApplicationId.resolve(fileName);

            Document document = documentService.save(Document.builder()
                    .applicationId(applicationId)
                    .documentFilePath(targetLocation.toString())
                    .documentFileSize(String.valueOf(file.getSize()))
                    .documentName(fileName)
                    .build());

            System.out.println(targetLocation.toString());
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            //return fileName;
            return document;
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!", ex);
        } /*catch (NoSuchAlgorithmException e) {
            throw new FileStorageException("No Such Algorithm", e);
            //e.printStackTrace();
        }*/
    }

    public Resource loadFileAsResource(String fileName, Long applicationId) {
        try {
            String fileStorageLocationPerApplicationId =
                    this.fileStorageLocation + File.separator + applicationId + File.separator + fileName;

            Path fileStoragePathPerApplicationId = Paths
                    .get(fileStorageLocationPerApplicationId)
                    .toAbsolutePath()
                    .normalize();
            //Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Path filePath = this.fileStorageLocation.resolve(fileStorageLocationPerApplicationId).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new MyFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new MyFileNotFoundException("File not found " + fileName, ex);
        }
    }
}