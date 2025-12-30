package com.edulink.repository;

import com.edulink.model.DocumentRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRatingRepository extends JpaRepository<DocumentRating, Long> {
    
    Optional<DocumentRating> findByDocumentDocumentIdAndUserUserId(Long documentId, Long userId);
    
    List<DocumentRating> findByDocumentDocumentId(Long documentId);
    
    @Query("SELECT AVG(r.rating) FROM DocumentRating r WHERE r.document.documentId = :documentId")
    Double getAverageRatingForDocument(Long documentId);
    
    @Query("SELECT COUNT(r) FROM DocumentRating r WHERE r.document.documentId = :documentId")
    Long countRatingsForDocument(Long documentId);
}

