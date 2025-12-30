package com.edulink.dto.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidationStatsDTO {
    private long pendingCount;
    private long approvedCount;
    private long totalCount;
}



