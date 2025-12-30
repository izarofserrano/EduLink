package com.edulink.repository;

import com.edulink.model.ForumThread;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ForumThreadRepository extends JpaRepository<ForumThread, Long> {
    List<ForumThread> findByCourse_CourseId(Long courseId);
    
    @Query("SELECT COUNT(t) FROM ForumThread t WHERE t.course.courseId = :courseId " +
           "AND NOT EXISTS (SELECT 1 FROM ForumReply r WHERE r.thread = t AND r.user.role = 'TEACHER')")
    long countByCourseAndUnanswered(@Param("courseId") Long courseId);
}