package com.example.filedemo.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "document")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Document {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "document_filesize",
            length = 50,
            nullable = false)
    private String documentFileSize;

    @Column(name = "document_filepath",
            length = 500,
            nullable = false)
    private String documentFilePath;

    @Column(name = "document_name",
            length = 500,
            nullable = false)
    private String documentName;
}
