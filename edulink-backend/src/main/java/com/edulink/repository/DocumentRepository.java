package com.edulink.repository;

import com.edulink.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCourse_CourseId(Long courseId);
    List<Document> findByIsApproved(Boolean isApproved);
    long countByUploaderUserId(Long userId);
    long countByIsApproved(Boolean isApproved);
    List<Document> findByUploaderUserIdAndIsApproved(Long userId, Boolean isApproved);

    @Query("SELECT CASE WHEN COUNT(d) > 0 THEN true ELSE false END " + 
    "FROM Document d WHERE d.documentId = :documentId AND d.isApproved = true") 
    boolean existsByIdAndApproved(Long documentId);


}
