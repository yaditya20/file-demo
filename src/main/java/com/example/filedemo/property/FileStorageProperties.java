package com.example.filedemo.property;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "file")
public class FileStorageProperties {
    private String uploadDir;

    public String getUploadDir() {
        System.out.println(uploadDir);
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        System.out.println(uploadDir);
        this.uploadDir = uploadDir;
    }
}