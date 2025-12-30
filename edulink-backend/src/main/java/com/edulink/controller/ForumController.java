package com.edulink.controller;

import com.edulink.dto.forum.*;
import com.edulink.service.ForumService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/forum")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ForumController {

    private static final Logger logger = LoggerFactory.getLogger(ForumController.class);
    
    private final ForumService forumService;

    /**
     * Get all threads (optionally filtered by course)
     */
    @GetMapping("/threads")
    public ResponseEntity<List<ForumThreadDTO>> getAllThreads(
            @RequestParam(required = false) Long courseId
    ) {
        List<ForumThreadDTO> threads = forumService.getAllThreads(courseId);
        return ResponseEntity.ok(threads);
    }

    /**
     * Get thread by ID with all replies
     */
    @GetMapping("/threads/{id}")
    public ResponseEntity<ForumThreadDetailDTO> getThreadById(@PathVariable Long id) {
        ForumThreadDetailDTO thread = forumService.getThreadById(id);
        return ResponseEntity.ok(thread);
    }

    /**
     * Create new thread (authenticated users only)
     */
    @PostMapping("/threads")
    public ResponseEntity<ForumThreadDTO> createThread(
            @Valid @RequestBody ForumThreadCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} creating thread", userDetails.getUsername());
        ForumThreadDTO thread = forumService.createThread(request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(thread);
    }

    /**
     * Post reply to thread (authenticated users only)
     */
    @PostMapping("/threads/{threadId}/replies")
    public ResponseEntity<ForumReplyDTO> createReply(
            @PathVariable Long threadId,
            @Valid @RequestBody ForumReplyCreateRequest request,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} replying to thread {}", userDetails.getUsername(), threadId);
        ForumReplyDTO reply = forumService.createReply(threadId, request, userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(reply);
    }

    /**
     * Mark thread as resolved (author or teacher only)
     */
    @PutMapping("/threads/{id}/resolve")
    public ResponseEntity<ForumThreadDTO> markAsResolved(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} marking thread {} as resolved", userDetails.getUsername(), id);
        ForumThreadDTO thread = forumService.markAsResolved(id, userDetails.getUsername());
        return ResponseEntity.ok(thread);
    }

    /**
     * Delete thread (author or admin only)
     */
    @DeleteMapping("/threads/{id}")
    public ResponseEntity<Map<String, String>> deleteThread(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} deleting thread {}", userDetails.getUsername(), id);
        forumService.deleteThread(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Thread deleted successfully"));
    }

    /**
     * Delete reply (author or admin only)
     */
    @DeleteMapping("/replies/{id}")
    public ResponseEntity<Map<String, String>> deleteReply(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails
    ) {
        logger.info("User {} deleting reply {}", userDetails.getUsername(), id);
        forumService.deleteReply(id, userDetails.getUsername());
        return ResponseEntity.ok(Map.of("message", "Reply deleted successfully"));
    }
}


