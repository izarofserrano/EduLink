package com.edulink.dto.document;

import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/***
 * Document Upload Request DTO
 */


public class DocumentUploadRequest {

    @NotNull
    private MultipartFile file;

    @NotBlank
    private String docTitle;

    @NotBlank
    private String docDescription;

    @NotNull
    private Long courseId;

    @NotBlank
    private String documentType;

   
}
