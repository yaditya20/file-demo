package com.example.filedemo.service;

import com.example.filedemo.model.Document;
import com.example.filedemo.repository.DocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    @Autowired
    private DocumentRepository documentRepository;

    public Document save(Document document){

        return documentRepository.save(document);
    }

    public Document getById(Long documentId){
        return documentRepository.findById(documentId).orElseThrow(() -> new RuntimeException("Records not found!"));
    }

    public Document getByApplicationId(Long applicationId){
        return documentRepository.findByApplicationId(applicationId);
    }
}
