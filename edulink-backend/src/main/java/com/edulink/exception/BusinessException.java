package com.edulink.exception;

import org.springframework.http.HttpStatus;

/**
 * Single custom exception for all business errors
 */
public class BusinessException extends RuntimeException {
    
    private final HttpStatus httpStatus;
    
    public BusinessException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
    
    public BusinessException(String message, HttpStatus httpStatus) {
        super(message);
        this.httpStatus = httpStatus;
    }
    
    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
    
    // ✅ Helper methods para casos comunes
    public static BusinessException notFound(String message) {
        return new BusinessException(message, HttpStatus.NOT_FOUND);
    }
    
    public static BusinessException conflict(String message) {
        return new BusinessException(message, HttpStatus.CONFLICT);
    }
    
    public static BusinessException forbidden(String message) {
        return new BusinessException(message, HttpStatus.FORBIDDEN);
    }
    
    public static BusinessException unauthorized(String message) {
        return new BusinessException(message, HttpStatus.UNAUTHORIZED);
    }
}


