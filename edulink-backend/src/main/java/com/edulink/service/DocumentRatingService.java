package com.edulink.service;


import com.edulink.dto.document.DocumentRatingDTO;
import com.edulink.exception.BusinessException;
import com.edulink.model.*;
import com.edulink.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional
public class DocumentRatingService {


    private static final Logger logger = LoggerFactory.getLogger(DocumentRatingService.class);


    private final DocumentRatingRepository ratingRepository;
    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;


    /**
     * Rate a document
     */
    public DocumentRatingDTO rateDocument(Long documentId, Integer rating, String username) {
        logger.info("User {} rating document {} with {} stars", username, documentId, rating);


        // Validate rating
        if (rating < 1 || rating > 5) {
            throw new BusinessException("Rating must be between 1 and 5");
        }


        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));


        // Get document
        Document document = documentRepository.findById(documentId)
                .orElseThrow(() -> BusinessException.notFound("Document not found with id: " + documentId));


        if (!document.getIsApproved()) {
            throw BusinessException.forbidden("Cannot rate unapproved document");
        }


        // Check if user already rated
        DocumentRating documentRating = ratingRepository
                .findByDocumentDocumentIdAndUserUserId(documentId, user.getUserId())
                .orElse(new DocumentRating());


        documentRating.setDocument(document);
        documentRating.setUser(user);
        documentRating.setRating(rating);


        ratingRepository.save(documentRating);


        // Update uploader reputation if student
        updateUploaderReputation(document);


        // Return updated rating info
        return getRating(documentId, username);
    }


    /**
     * Get document rating
     */
    @Transactional(readOnly = true)
    public DocumentRatingDTO getRating(Long documentId, String username) {
        Double avgRating = ratingRepository.getAverageRatingForDocument(documentId);
        Long totalRatings = ratingRepository.countRatingsForDocument(documentId);


        Integer userRating = null;
        if (username != null) {
            User user = userRepository.findByUsername(username).orElse(null);
            if (user != null) {
                userRating = ratingRepository
                        .findByDocumentDocumentIdAndUserUserId(documentId, user.getUserId())
                        .map(DocumentRating::getRating)
                        .orElse(null);
            }
        }


        return DocumentRatingDTO.builder()
                .averageRating(avgRating != null ? avgRating : 0.0)
                .totalRatings(totalRatings)
                .userRating(userRating)
                .build();
    }


    /**
     * Update uploader reputation based on ratings
     */
    private void updateUploaderReputation(Document document) {
        User uploader = document.getUploader();


        if (!(uploader instanceof Student)) {
            return;
        }


        Student student = (Student) uploader;


        // Calculate total reputation from all documents
        double totalReputation = documentRepository.findAll().stream()
                .filter(doc -> doc.getUploader().getUserId().equals(student.getUserId()))
                .mapToDouble(doc -> {
                    Double avgRating = ratingRepository.getAverageRatingForDocument(doc.getDocumentId());
                    return avgRating != null ? avgRating * 20 : 0.0; // 20 points per star
                })
                .sum();


        student.setReputationPoints(totalReputation);
        userRepository.save(student);


        logger.info("Updated reputation for student {}: {} points", 
                    student.getUsername(), totalReputation);
    }
}



