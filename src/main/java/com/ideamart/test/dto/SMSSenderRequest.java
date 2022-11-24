package com.ideamart.test.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SMSSenderRequest {
    private String message;
    private String destinationAddresses;
    private String password;
    private String applicationId;
}
