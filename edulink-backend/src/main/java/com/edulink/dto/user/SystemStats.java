package com.edulink.dto.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SystemStats {
    private long totalUsers;
    private long totalStudents;
    private long totalTeachers;
    private long totalAdmins;
    private long totalDocuments;
    private long totalApprovedDocuments;
    private long totalPendingDocuments;
}


