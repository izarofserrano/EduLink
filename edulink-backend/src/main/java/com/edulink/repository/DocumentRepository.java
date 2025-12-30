package com.edulink.repository;

import com.edulink.model.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {
    List<Document> findByCourse_CourseId(Long courseId);
    List<Document> findByIsApproved(Boolean isApproved);
    long countByUploaderUserId(Long userId);
    long countByIsApproved(Boolean isApproved);
}
