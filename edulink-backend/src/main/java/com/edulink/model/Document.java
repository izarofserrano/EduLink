package com.edulink.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Document {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long documentId;
    
    @ManyToOne
    @JoinColumn(name = "uploader_id", nullable = false)
    private User uploader;
    
    @ManyToOne
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;
    
    @Column(nullable = false)
    private String docTitle;
    
    @Column(columnDefinition = "TEXT")
    private String docDescription;
    
    private Integer downloadCount = 0;
    
    private Boolean isApproved = false;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    // File storage URL (e.g., S3 URL)
    private String fileUrl;

    private String documentType;
    
    @PrePersist
    protected void onCreate() {
        uploadedAt = LocalDateTime.now();
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
