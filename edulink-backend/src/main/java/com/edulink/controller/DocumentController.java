package com.edulink.controller;


import com.edulink.dto.document.*;
import com.edulink.service.DocumentRatingService;
import com.edulink.service.DocumentService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentController {


    private static final Logger logger = LoggerFactory.getLogger(DocumentController.class);


    private final DocumentService documentService;
    private final DocumentRatingService ratingService;


    /**
     * Get all approved documents
     */
    @GetMapping
    public ResponseEntity<List<DocumentDTO>> getAllDocuments() {
        List<DocumentDTO> documents = documentService.getAllDocuments();
        return ResponseEntity.ok(documents);
    }


    /**
     * Get document by ID with details
     */
    @GetMapping("/{id}")
    public ResponseEntity<DocumentDetailDTO> getDocumentById(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        DocumentDetailDTO document = documentService.getDocumentById(id, username);
        return ResponseEntity.ok(document);
    }


    /**
     * Get documents by course
     */
    @GetMapping("/course/{courseId}")
    public ResponseEntity<List<DocumentDTO>> getDocumentsByCourse(@PathVariable Long courseId) {
        List<DocumentDTO> documents = documentService.getDocumentsByCourse(courseId);
        return ResponseEntity.ok(documents);
    }


    /**
     * Upload new document
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentDTO> uploadDocument(
            @RequestParam("file") MultipartFile file,
            @RequestParam("docTitle") String docTitle,
            @RequestParam("docDescription") String docDescription,
            @RequestParam("courseId") Long courseId,
            @RequestParam("documentType") String documentType,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} uploading document: {}", userDetails.getUsername(), docTitle);


        DocumentDTO document = documentService.uploadDocument(
                file,
                docTitle,
                docDescription,
                courseId,
                documentType,
                userDetails.getUsername()
        );


        return ResponseEntity.status(HttpStatus.CREATED).body(document);
    }


    /**
     * Preview document (inline display)
     */
    @GetMapping("/{documentId}/preview")
    public ResponseEntity<Resource> previewDocument(@PathVariable Long documentId) {
        logger.info("Previewing document: {}", documentId);

        var fileData = documentService.previewDocument(documentId);


        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(fileData.getContentType())) 
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + fileData.getFileName() + "\"") 
                .header(HttpHeaders.CACHE_CONTROL, "no-cache, no-store, must-revalidate") .header(HttpHeaders.PRAGMA, "no-cache") 
                .header(HttpHeaders.EXPIRES, "0") .header("X-Frame-Options", "SAMEORIGIN") 
                .contentLength(fileData.getFileSize()) 
                .body(fileData.getResource());

    }


    /**
     * Download document (attachment)
     */
    @GetMapping("/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} downloading document: {}", userDetails.getUsername(), documentId);


        var fileData = documentService.downloadDocument(documentId, userDetails.getUsername());


        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileData.getFileName() + "\"")
                .contentType(MediaType.parseMediaType(fileData.getContentType()))
                .contentLength(fileData.getFileSize())
                .header("Cache-Control", "no-cache, no-store, must-revalidate")
                .header("Pragma", "no-cache")
                .header("Expires", "0")
                .body(fileData.getResource());
    }


    /**
     * Rate a document
     */
    @PostMapping("/{documentId}/rate")
    public ResponseEntity<DocumentRatingDTO> rateDocument(
            @PathVariable Long documentId,
            @RequestBody Map<String, Integer> request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        Integer rating = request.get("rating");
        
        if (rating == null || rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }


        DocumentRatingDTO ratingDTO = ratingService.rateDocument(
                documentId,
                rating,
                userDetails.getUsername()
        );


        return ResponseEntity.ok(ratingDTO);
    }


    /**
     * Get document rating
     */
    @GetMapping("/{documentId}/rating")
    public ResponseEntity<DocumentRatingDTO> getDocumentRating(
            @PathVariable Long documentId,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        String username = userDetails != null ? userDetails.getUsername() : null;
        DocumentRatingDTO rating = ratingService.getRating(documentId, username);
        return ResponseEntity.ok(rating);
    }


    /**
     * Delete document
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDocument(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} deleting document: {}", userDetails.getUsername(), id);
        documentService.deleteDocument(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Document deleted successfully"));
    }
}



