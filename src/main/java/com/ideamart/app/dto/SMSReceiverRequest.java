package com.ideamart.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSReceiverRequest {
    private String version;
    private String applicationId;
    private String sourceAddress;
    private String message;
    private String requestId;
    private String encoding;
}
