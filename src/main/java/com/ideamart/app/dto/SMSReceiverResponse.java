package com.ideamart.app.dto;

import com.ideamart.app.constants.SmsReceiverResponseCode;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSReceiverResponse {
    private SmsReceiverResponseCode statusCode;
    private String statusDetail;
}
