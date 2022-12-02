package com.ideamart.app.util;

import com.ideamart.app.constant.Message;
import com.ideamart.app.constant.RequestType;
import com.ideamart.app.dto.SMSSenderRequest;
import com.ideamart.app.dto.SMSSenderResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MessageUtils {
    @Value("${SERVERURL}")
    private String serverUrl;
    @Value("${PASSWORD}")
    private String password;
    @Value("${APPLICATIONID}")
    private String applicationId;
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String message, String destinationAddress) {
        SMSSenderRequest smsSenderRequest = new SMSSenderRequest(message, destinationAddress, applicationId, password);
        restTemplate.postForObject(serverUrl, smsSenderRequest, SMSSenderResponse.class);
    }

    public RequestType getRequestType(String message, String address) {
        if (message.matches("TEST\\s+REG")) {
            return RequestType.REGISTER;
        } else if (message.matches("TEST\\s+SCORE")) {
            return RequestType.SCORE;
        } else if (message.matches("TEST\\s+Q\\d\\s+ANS\\d")) {
            return RequestType.ANSWER;
        } else if (message.matches("TEST\\s+Q\\d")) {
            return RequestType.QUESTION;
        } else if (message.matches("TEST\\s+LEADERBOARD")) {
            return RequestType.LEADERBOARD;
        } else {
            sendMessage(Message.INVALID_REQUEST.toString(), address);
            return RequestType.INVALID;
        }
    }

    public int retrieveQuestionNo(String message) {
        String stringWithoutWhiteSpaces = message.replaceAll("\\s", "");
        return Integer.parseInt(stringWithoutWhiteSpaces.substring(5, 6));
    }

    public int retrieveAnswerNo(String message) {
        String stringWithoutWhiteSpaces = message.replaceAll("\\s", "");
        return Integer.parseInt(stringWithoutWhiteSpaces.substring(9, 10));
    }
}
