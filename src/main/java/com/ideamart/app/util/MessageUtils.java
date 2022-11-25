package com.ideamart.app.util;

import com.ideamart.app.constants.Message;
import com.ideamart.app.constants.RequestType;
import com.ideamart.app.dto.SMSSenderRequest;
import com.ideamart.app.dto.SMSSenderResponse;
import com.ideamart.app.exception.InvalidRequestException;
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

    public RequestType getRequestType(String message) {
        if (message.contains("REG")) {
            return RequestType.REGISTER;
        } else if (message.contains("SCORE")) {
            return RequestType.SCORE;
        } else if (message.contains("Q") && message.contains("ANS")) {
            return RequestType.ANSWER;
        } else if (message.contains("Q")) {
            return RequestType.QUESTION;
        } else if (message.contains("LEADERBOARD")) {
            return RequestType.LEADERBOARD;
        } else {
            return RequestType.INVALID;
        }
    }

    public void checkValidityOfMessage(String message, String address) {
        if (message.isEmpty()) {
            sendMessage(Message.EMPTYMESSAGE.toString(), address);
            throw new InvalidRequestException();
        } else if (!message.contains("TEST")) {
            sendMessage(Message.NOTCONTAINSTEST.toString(), address);
            throw new InvalidRequestException();
        } else if (!message.startsWith("TEST")) {
            sendMessage(Message.NOTSTARTSWITHTEST.toString(), address);
            throw new InvalidRequestException();
        }
    }

    public int retrieveQuestionNo(String message) {
        String stringWithoutWhiteSpaces = message.replaceAll("\\s", "");
        return Integer.parseInt(stringWithoutWhiteSpaces.substring(1, 2));
    }

    public int retrieveAnswerNo(String message) {
        String stringWithoutWhiteSpaces = message.replaceAll("\\s", "");
        return Integer.parseInt(stringWithoutWhiteSpaces.substring(5));
    }
}
