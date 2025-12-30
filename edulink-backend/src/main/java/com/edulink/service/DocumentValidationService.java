package com.edulink.service;


import com.edulink.dto.document.ValidationStatsDTO;
import com.edulink.exception.BusinessException;
import com.edulink.model.Document;
import com.edulink.model.User;
import com.edulink.model.enums.UserRole;
import com.edulink.repository.DocumentRepository;
import com.edulink.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Service
@RequiredArgsConstructor
@Transactional
public class DocumentValidationService {


    private static final Logger logger = LoggerFactory.getLogger(DocumentValidationService.class);


    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;


    /**
     * Get all documents for admin validation (pending first)
     */
    @Transactional(readOnly = true)
    public List<Document> getDocumentsForValidation(String adminUsername) {
        logger.info("Admin {} requesting documents for validation", adminUsername);
        verifyAdminAccess(adminUsername);


        List<Document> allDocuments = documentRepository.findAll();


        // Separate and sort
        List<Document> pending = allDocuments.stream()
                .filter(doc -> !doc.getIsApproved())
                .sorted(Comparator.comparing(Document::getUploadedAt).reversed())
                .collect(Collectors.toList());


        List<Document> approved = allDocuments.stream()
                .filter(Document::getIsApproved)
                .sorted(Comparator.comparing(Document::getUploadedAt).reversed())
                .collect(Collectors.toList());


        return Stream.concat(pending.stream(), approved.stream())
                .collect(Collectors.toList());
    }


    /**
     * Approve document
     */
    public Document approveDocument(Long documentId, String adminUsername) {
        logger.info("Admin {} approving document {}", adminUsername, documentId);
        verifyAdminAccess(adminUsername);


        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Document not found with id: " + documentId));


        document.setIsApproved(true);
        return documentRepository.save(document);
    }


    /**
     * Reject (delete) document
     */
    public void rejectDocument(Long documentId, String adminUsername) {
        logger.info("Admin {} rejecting document {}", adminUsername, documentId);
        verifyAdminAccess(adminUsername);


        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Document not found with id: " + documentId));


        documentRepository.delete(document);
    }


    /**
     * Get validation statistics
     */
    @Transactional(readOnly = true)
    public ValidationStatsDTO getValidationStats(String adminUsername) {
        verifyAdminAccess(adminUsername);


        long pending = documentRepository.countByIsApproved(false);
        long approved = documentRepository.countByIsApproved(true);


        return ValidationStatsDTO.builder()
                .pendingCount(pending)
                .approvedCount(approved)
                .totalCount(pending + approved)
                .build();
    }


    private void verifyAdminAccess(String username) {
        User admin = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));


        if (admin.getRole() != UserRole.ADMIN) {
            throw BusinessException.forbidden("Admin role required");
        }
    }
}



