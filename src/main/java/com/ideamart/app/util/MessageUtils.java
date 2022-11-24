package com.ideamart.app.util;

import com.ideamart.app.dto.SMSSenderRequest;
import com.ideamart.app.dto.SMSSenderResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MessageUtils {
    private static final String SERVERURL = "http://localhost:7000/sms/send";
    private static final String PASSWORD = "password";
    private static final String APPLICATIONID="APP_000001";
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String message, String destinationAddress){
        SMSSenderRequest smsSenderRequest = new SMSSenderRequest(message, destinationAddress, APPLICATIONID, PASSWORD);
        restTemplate.postForObject(SERVERURL, smsSenderRequest, SMSSenderResponse.class);
    }
}
