package com.edulink.controller;


import com.edulink.dto.document.ValidationStatsDTO;
import com.edulink.model.Document;
import com.edulink.service.DocumentValidationService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/admin/documents")
@RequiredArgsConstructor
public class AdminDocumentController {


    private static final Logger logger = LoggerFactory.getLogger(AdminDocumentController.class);


    private final DocumentValidationService validationService;


    /**
     * Get all documents for validation (pending first)
     */
    @GetMapping("/validate")
    public ResponseEntity<List<Document>> getDocumentsForValidation(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} requesting documents for validation", userDetails.getUsername());
        List<Document> documents = validationService.getDocumentsForValidation(userDetails.getUsername());
        return ResponseEntity.ok(documents);
    }


    /**
     * Approve a document
     */
    @PostMapping("/{id}/approve")
    public ResponseEntity<Map<String, Object>> approveDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} approving document {}", userDetails.getUsername(), id);
        Document approved = validationService.approveDocument(id, userDetails.getUsername());
        
        return ResponseEntity.ok(Map.of(
                "message", "Document approved successfully",
                "document", approved
        ));
    }


    /**
     * Reject (delete) a document
     */
    @DeleteMapping("/{id}/reject")
    public ResponseEntity<Map<String, String>> rejectDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} rejecting document {}", userDetails.getUsername(), id);
        validationService.rejectDocument(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Document rejected and deleted successfully"));
    }


    /**
     * Get validation statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<ValidationStatsDTO> getValidationStats(
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("Admin {} requesting validation stats", userDetails.getUsername());
        ValidationStatsDTO stats = validationService.getValidationStats(userDetails.getUsername());
        return ResponseEntity.ok(stats);
    }
}



