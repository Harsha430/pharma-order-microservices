package com.pharmaorder.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

public class ErrorResponse {
    private int status;
    private String message;
    private String errorCode;
    private java.time.LocalDateTime timestamp;

    public ErrorResponse() {}

    public ErrorResponse(int status, String message, String errorCode, java.time.LocalDateTime timestamp) {
        this.status = status;
        this.message = message;
        this.errorCode = errorCode;
        this.timestamp = timestamp;
    }

    public static ErrorResponse builder() {
        return new ErrorResponse();
    }
    
    public ErrorResponse status(int status) { this.status = status; return this; }
    public ErrorResponse message(String message) { this.message = message; return this; }
    public ErrorResponse errorCode(String errorCode) { this.errorCode = errorCode; return this; }
    public ErrorResponse timestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; return this; }
    public ErrorResponse build() { return this; }

    // Manual Getters
    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getErrorCode() { return errorCode; }
    public java.time.LocalDateTime getTimestamp() { return timestamp; }
    
    // Manual Setters
    public void setStatus(int status) { this.status = status; }
    public void setMessage(String message) { this.message = message; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }
    public void setTimestamp(java.time.LocalDateTime timestamp) { this.timestamp = timestamp; }
}
