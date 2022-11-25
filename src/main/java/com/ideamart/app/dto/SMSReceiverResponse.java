package com.ideamart.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSReceiverResponse {
    private String statusCode;
    private String statusDetail;
}
