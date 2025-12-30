package com.edulink.service;


import com.edulink.dto.document.*;
import com.edulink.exception.BusinessException;
import com.edulink.model.*;
import com.edulink.model.enums.UserRole;
import com.edulink.repository.*;


import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Transactional
public class DocumentService {


    private static final Logger logger = LoggerFactory.getLogger(DocumentService.class);


    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;
    private final DocumentRatingRepository ratingRepository;
    private final FileStorageService fileStorageService;


    /**
     * Get all approved documents
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> getAllDocuments() {
        return documentRepository.findByIsApproved(true).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * Get document by ID with full details
     */
    @Transactional(readOnly = true)
    public DocumentDetailDTO getDocumentById(Long documentId, String username) {
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Document not found with id: " + documentId));


        if (!document.getIsApproved()) {
            throw BusinessException.forbidden("Document is not approved yet");
        }


        return convertToDetailDTO(document, username);
    }


    /**
     * Get documents by course
     */
    @Transactional(readOnly = true)
    public List<DocumentDTO> getDocumentsByCourse(Long courseId) {
        List<Document> documents = documentRepository.findByCourse_CourseId(courseId);
        
        return documents.stream()
                .filter(Document::getIsApproved)
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }


    /**
     * Upload new document
     */
    public DocumentDTO uploadDocument(
            MultipartFile file,
            String docTitle,
            String docDescription,
            Long courseId,
            String documentType,
            String username
    ) {
        logger.info("User {} uploading document: {}", username, docTitle);


        // Get user
        User uploader = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));


        // Get course
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> BusinessException.notFound("Course not found with id: " + courseId));


        // Store file
        String storedFilename;
        try {
            storedFilename = fileStorageService.storeFile(file);
        } catch (Exception e) {
            throw new BusinessException("Failed to store file: " + e.getMessage());
        }


        // Create document
        Document document = new Document();
        document.setDocTitle(docTitle);
        document.setDocDescription(docDescription);
        document.setUploader(uploader);
        document.setCourse(course);
        document.setDocumentType(documentType);
        document.setFileUrl("uploads/" + storedFilename);
        document.setDownloadCount(0);
        document.setUploadedAt(LocalDateTime.now());


        // Auto-approve if teacher
        boolean isTeacher = uploader.getRole() == UserRole.TEACHER;
        document.setIsApproved(isTeacher);


        Document savedDocument = documentRepository.save(document);
        logger.info("Document uploaded with ID: {}", savedDocument.getDocumentId());


        return convertToDTO(savedDocument);
    }


    /**
     * Preview document (returns file for inline display)
     */
    public DocumentFileDTO previewDocument(Long documentId) {
        logger.info("Previewing document: {}", documentId);


        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Document not found with id: " + documentId));


        if (!document.getIsApproved()) {
            throw BusinessException.forbidden("Document is not approved yet");
        }


        return loadDocumentFile(document, false);
    }


    /**
     * Download document (increments counter, returns file for download)
     */
    public DocumentFileDTO downloadDocument(Long documentId, String username) {
        logger.info("User {} downloading document: {}", username, documentId);


        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Document not found with id: " + documentId));


        if (!document.getIsApproved()) {
            throw BusinessException.forbidden("Document is not approved yet");
        }


        // Increment download count
        document.setDownloadCount(document.getDownloadCount() + 1);
        documentRepository.save(document);


        return loadDocumentFile(document, true);
    }


    /**
     * Delete document
     */
    public void deleteDocument(Long documentId, String username) {
        logger.info("User {} deleting document: {}", username, documentId);


        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Document not found with id: " + documentId));


        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));


        // Only uploader or admin can delete
        if (!document.getUploader().getUserId().equals(user.getUserId()) && 
            user.getRole() != UserRole.ADMIN) {
            throw BusinessException.forbidden("You don't have permission to delete this document");
        }


        documentRepository.delete(document);
        logger.info("Document {} deleted", documentId);
    }


    // ==================== HELPER METHODS ====================


    private DocumentFileDTO loadDocumentFile(Document document, boolean isDownload) {
        try {
            File file = new File(document.getFileUrl());
            if (!file.exists()) {
                throw BusinessException.notFound("File not found: " + document.getFileUrl());
            }


            Path path = file.toPath();
            ByteArrayResource resource = new ByteArrayResource(Files.readAllBytes(path));


            String contentType = determineContentType(file.getName());
            String fileName = isDownload ? 
                    sanitizeFileName(document.getDocTitle()) + getFileExtension(file.getName()) : 
                    file.getName();


            return DocumentFileDTO.builder()
                    .resource(resource)
                    .fileName(fileName)
                    .contentType(contentType)
                    .fileSize(file.length())
                    .isDownload(isDownload)
                    .build();


        } catch (Exception e) {
            throw new BusinessException("Failed to load file: " + e.getMessage());
        }
    }


    private String determineContentType(String fileName) {
        String lowerFileName = fileName.toLowerCase();
        if (lowerFileName.endsWith(".pdf")) return "application/pdf";
        if (lowerFileName.endsWith(".doc") || lowerFileName.endsWith(".docx")) return "application/msword";
        if (lowerFileName.endsWith(".txt")) return "text/plain";
        if (lowerFileName.endsWith(".jpg") || lowerFileName.endsWith(".jpeg")) return "image/jpeg";
        if (lowerFileName.endsWith(".png")) return "image/png";
        return "application/octet-stream";
    }


    private String sanitizeFileName(String fileName) {
        return fileName.replaceAll("[^a-zA-Z0-9._-]", "_");
    }


    private String getFileExtension(String fileName) {
        int lastDot = fileName.lastIndexOf('.');
        return lastDot > 0 ? fileName.substring(lastDot) : "";
    }


    private DocumentDTO convertToDTO(Document document) {
        Double avgRating = ratingRepository.getAverageRatingForDocument(document.getDocumentId());
        Long totalRatings = ratingRepository.countRatingsForDocument(document.getDocumentId());


        return DocumentDTO.builder()
                .documentId(document.getDocumentId())
                .docTitle(document.getDocTitle())
                .docDescription(document.getDocDescription())
                .documentType(document.getDocumentType())
                .uploadedAt(document.getUploadedAt())
                .isApproved(document.getIsApproved())
                .downloadCount(document.getDownloadCount())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .totalRatings(totalRatings)
                .courseId(document.getCourse().getCourseId())
                .courseName(document.getCourse().getCourseName())
                .uploaderId(document.getUploader().getUserId())
                .uploaderUsername(document.getUploader().getUsername())
                .build();
    }


    private DocumentDetailDTO convertToDetailDTO(Document document, String username) {
        Double avgRating = ratingRepository.getAverageRatingForDocument(document.getDocumentId());
        Long totalRatings = ratingRepository.countRatingsForDocument(document.getDocumentId());


        // Get user's rating if authenticated
        Integer userRating = null;
        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                userRating = ratingRepository.findByDocumentDocumentIdAndUserUserId(
                        document.getDocumentId(), user.getUserId())
                        .map(DocumentRating::getRating)
                        .orElse(null);
            }
        }


        return DocumentDetailDTO.builder()
                .documentId(document.getDocumentId())
                .docTitle(document.getDocTitle())
                .docDescription(document.getDocDescription())
                .documentType(document.getDocumentType())
                .fileUrl(document.getFileUrl())
                .uploadedAt(document.getUploadedAt())
                .isApproved(document.getIsApproved())
                .downloadCount(document.getDownloadCount())
                .averageRating(avgRating != null ? avgRating : 0.0)
                .totalRatings(totalRatings)
                .userRating(userRating)
                .courseId(document.getCourse().getCourseId())
                .courseName(document.getCourse().getCourseName())
                .courseCode(document.getCourse().getCode())
                .uploaderId(document.getUploader().getUserId())
                .uploaderUsername(document.getUploader().getUsername())
                .uploaderRole(document.getUploader().getRole().toString())
                .build();
    }
}






