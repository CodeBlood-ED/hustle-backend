package com.hustle.backend.modules.user.dto;

import com.hustle.backend.modules.user.entity.EnquiryType;
import java.time.LocalDateTime;

public class EnquiryDto {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String subject;
    private String message;
    private EnquiryType type;
    private String status;
    private LocalDateTime createdAt;

    public EnquiryDto() {
    }

    public EnquiryDto(Long id, String customerName, String customerEmail, String subject, String message, EnquiryType type, String status, LocalDateTime createdAt) {
        this.id = id;
        this.customerName = customerName;
        this.customerEmail = customerEmail;
        this.subject = subject;
        this.message = message;
        this.type = type;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public EnquiryType getType() {
        return type;
    }

    public void setType(EnquiryType type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
