package com.hustle.backend.modules.user.dto;

import com.hustle.backend.modules.user.entity.EnquiryType;
import jakarta.validation.constraints.NotBlank;

public class CreateEnquiryRequest {

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Message details are required")
    private String message;

    private EnquiryType type = EnquiryType.SERVICE_REQUEST;

    public CreateEnquiryRequest() {
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
}
