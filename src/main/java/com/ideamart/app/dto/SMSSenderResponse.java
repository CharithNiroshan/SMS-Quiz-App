package com.ideamart.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SMSSenderResponse {
    private String statusCode;
    private String statusDetail;
    private String messageId;
    private String version;
}
