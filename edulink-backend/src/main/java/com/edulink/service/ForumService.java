package com.edulink.service;

import com.edulink.dto.forum.*;
import com.edulink.exception.BusinessException;
import com.edulink.model.*;
import com.edulink.model.enums.ThreadStatus;
import com.edulink.model.enums.UserRole;
import com.edulink.repository.*;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ForumService {

    private static final Logger logger = LoggerFactory.getLogger(ForumService.class);

    private final ForumThreadRepository threadRepository;
    private final ForumReplyRepository replyRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    /**
     * Get all threads (optionally filtered by course)
     */
    @Transactional(readOnly = true)
    public List<ForumThreadDTO> getAllThreads(Long courseId) {
        List<ForumThread> threads;
        
        if (courseId != null) {
            threads = threadRepository.findByCourse_CourseId(courseId);
        } else {
            threads = threadRepository.findAll();
        }
        
        return threads.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    /**
     * Get thread by ID with all replies
     */
    @Transactional(readOnly = true)
    public ForumThreadDetailDTO getThreadById(Long threadId) {
        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> BusinessException.notFound("Thread not found with id: " + threadId));
        
        // Increment view count
        thread.setViews(thread.getViews() + 1);
        threadRepository.save(thread);
        
        return convertToDetailDTO(thread);
    }

    /**
     * Create new thread
     */
    public ForumThreadDTO createThread(ForumThreadCreateRequest request, String username) {
        logger.info("User {} creating forum thread: {}", username, request.getTitle());

        // Get author
        User author = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));

        // Get course
        Course course = courseRepository.findById(request.getCourseId())
                .orElseThrow(() -> BusinessException.notFound("Course not found with id: " + request.getCourseId()));

        // Create thread
        ForumThread thread = new ForumThread();
        thread.setTitle(request.getTitle());
        thread.setContent(request.getContent());
        thread.setAuthor(author);
        thread.setCourse(course);
        thread.setStatus(ThreadStatus.OPEN);
        thread.setViews(0);
        thread.setCreatedAt(LocalDateTime.now());

        ForumThread savedThread = threadRepository.save(thread);
        logger.info("Forum thread created with ID: {}", savedThread.getThreadId());

        return convertToDTO(savedThread);
    }

    /**
     * Post reply to thread
     */
    public ForumReplyDTO createReply(Long threadId, ForumReplyCreateRequest request, String username) {
        logger.info("User {} replying to thread {}", username, threadId);

        // Get thread
        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> BusinessException.notFound("Thread not found with id: " + threadId));

        // Get user
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));

        // Create reply
        ForumReply reply = new ForumReply();
        reply.setContent(request.getContent());
        reply.setThread(thread);
        reply.setUser(user);
        reply.setIsTeacherResponse(user.getRole() == UserRole.TEACHER);
        reply.setCreatedAt(LocalDateTime.now());

        ForumReply savedReply = replyRepository.save(reply);
        logger.info("Reply created with ID: {}", savedReply.getReplyId());

        return convertReplyToDTO(savedReply);
    }

    /**
     * Mark thread as resolved
     */
    public ForumThreadDTO markAsResolved(Long threadId, String username) {
        logger.info("User {} marking thread {} as resolved", username, threadId);

        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> BusinessException.notFound("Thread not found with id: " + threadId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));

        // Only author or teacher can mark as resolved
        if (!thread.getAuthor().getUserId().equals(user.getUserId()) && 
            user.getRole() != UserRole.TEACHER) {
            throw BusinessException.forbidden("Only the author or a teacher can mark this thread as resolved");
        }

        thread.setStatus(ThreadStatus.RESOLVED);
        ForumThread updatedThread = threadRepository.save(thread);

        return convertToDTO(updatedThread);
    }

    /**
     * Delete thread (author or admin only)
     */
    public void deleteThread(Long threadId, String username) {
        logger.info("User {} deleting thread {}", username, threadId);

        ForumThread thread = threadRepository.findById(threadId)
                .orElseThrow(() -> BusinessException.notFound("Thread not found with id: " + threadId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));

        // Only author or admin can delete
        if (!thread.getAuthor().getUserId().equals(user.getUserId()) && 
            user.getRole() != UserRole.ADMIN) {
            throw BusinessException.forbidden("Only the author or an admin can delete this thread");
        }

        threadRepository.delete(thread);
        logger.info("Thread {} deleted", threadId);
    }

    /**
     * Delete reply (author or admin only)
     */
    public void deleteReply(Long replyId, String username) {
        logger.info("User {} deleting reply {}", username, replyId);

        ForumReply reply = replyRepository.findById(replyId)
                .orElseThrow(() -> BusinessException.notFound("Reply not found with id: " + replyId));

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> BusinessException.notFound("User not found: " + username));

        // Only author or admin can delete
        if (!reply.getUser().getUserId().equals(user.getUserId()) && 
            user.getRole() != UserRole.ADMIN) {
            throw BusinessException.forbidden("Only the author or an admin can delete this reply");
        }

        replyRepository.delete(reply);
        logger.info("Reply {} deleted", replyId);
    }

    // Helper methods

    /**
     * Convert ForumThread to ForumThreadDTO
     * @param thread
     * @return
     */
    private ForumThreadDTO convertToDTO(ForumThread thread) {
        boolean hasTeacherResponse = thread.getReplies().stream()
                .anyMatch(reply -> reply.getUser().getRole() == UserRole.TEACHER);

        return ForumThreadDTO.builder()
                .threadId(thread.getThreadId())
                .title(thread.getTitle())
                .content(thread.getContent())
                .status(thread.getStatus().toString())
                .views(thread.getViews())
                .replyCount(thread.getReplies().size())
                .hasTeacherResponse(hasTeacherResponse)
                .createdAt(thread.getCreatedAt())
                .authorId(thread.getAuthor().getUserId())
                .authorUsername(thread.getAuthor().getUsername())
                .authorRole(thread.getAuthor().getRole().toString())
                .courseId(thread.getCourse().getCourseId())
                .courseName(thread.getCourse().getCourseName())
                .courseCode(thread.getCourse().getCode())
                .build();
    }

    /**
     * Convert ForumThread to ForumThreadDetailDTO
     * @param thread
     * @return
     */
    private ForumThreadDetailDTO convertToDetailDTO(ForumThread thread) {
        boolean hasTeacherResponse = thread.getReplies().stream()
                .anyMatch(reply -> reply.getUser().getRole() == UserRole.TEACHER);

        List<ForumReplyDTO> replies = thread.getReplies().stream()
                .map(this::convertReplyToDTO)
                .collect(Collectors.toList());

        return ForumThreadDetailDTO.builder()
                .threadId(thread.getThreadId())
                .title(thread.getTitle())
                .content(thread.getContent())
                .status(thread.getStatus().toString())
                .views(thread.getViews())
                .hasTeacherResponse(hasTeacherResponse)
                .createdAt(thread.getCreatedAt())
                .authorId(thread.getAuthor().getUserId())
                .authorUsername(thread.getAuthor().getUsername())
                .authorRole(thread.getAuthor().getRole().toString())
                .courseId(thread.getCourse().getCourseId())
                .courseName(thread.getCourse().getCourseName())
                .courseCode(thread.getCourse().getCode())
                .replies(replies)
                .build();
    }

    /**
     * Convert ForumReply to ForumReplyDTO
     * @param reply
     * @return
     */
    private ForumReplyDTO convertReplyToDTO(ForumReply reply) {
        return ForumReplyDTO.builder()
                .replyId(reply.getReplyId())
                .content(reply.getContent())
                .isTeacherResponse(reply.getIsTeacherResponse())
                .createdAt(reply.getCreatedAt())
                .userId(reply.getUser().getUserId())
                .username(reply.getUser().getUsername())
                .userRole(reply.getUser().getRole().toString())
                .threadId(reply.getThread().getThreadId())
                .build();
    }
}


