package com.edulink.dto.document;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.core.io.Resource;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentFileDTO {
    private Resource resource;
    private String fileName;
    private String contentType;
    private long fileSize;
    private boolean isDownload;
}