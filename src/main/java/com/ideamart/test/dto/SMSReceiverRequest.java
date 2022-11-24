package com.ideamart.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSReceiverRequest {
    private String applicationId;
    private String sourceAddress;
    private String message;
    private String requestId;
    private String version;
    private String encoding;
}
