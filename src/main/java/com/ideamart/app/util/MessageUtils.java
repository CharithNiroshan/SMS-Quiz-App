package com.ideamart.app.util;

import com.ideamart.app.constants.Message;
import com.ideamart.app.constants.RequestType;
import com.ideamart.app.dto.SMSSenderRequest;
import com.ideamart.app.dto.SMSSenderResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class MessageUtils {
    private static final String SERVERURL = "http://localhost:7000/sms/send";
    private static final String PASSWORD = "password";
    private static final String APPLICATIONID = "APP_000001";
    private final RestTemplate restTemplate = new RestTemplate();

    public void sendMessage(String message, String destinationAddress) {
        SMSSenderRequest smsSenderRequest = new SMSSenderRequest(message, destinationAddress, APPLICATIONID, PASSWORD);
        restTemplate.postForObject(SERVERURL, smsSenderRequest, SMSSenderResponse.class);
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
            sendMessage(Message.INVALIDREQUEST.toString(), address);
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
