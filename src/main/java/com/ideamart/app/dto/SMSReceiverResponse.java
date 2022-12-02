package com.ideamart.app.dto;

import com.ideamart.app.constant.SmsReceiverResponseCode;
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
