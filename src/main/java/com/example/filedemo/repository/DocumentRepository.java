package com.example.filedemo.repository;

import com.example.filedemo.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    public Document findByApplicationId(Long applicationId);
}
