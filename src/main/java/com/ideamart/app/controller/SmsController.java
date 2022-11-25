package com.ideamart.app.controller;

import com.ideamart.app.dto.SMSReceiverRequest;
import com.ideamart.app.dto.SMSReceiverResponse;
import com.ideamart.app.exception.*;
import com.ideamart.app.service.SmsService;
import com.ideamart.app.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Slf4j
@Controller
public class SmsController {
    private final SmsService smsService;
    private final MessageUtils messageUtils;

    @Autowired
    public SmsController(SmsService smsService, MessageUtils messageUtils) {
        this.smsService = smsService;
        this.messageUtils = messageUtils;
    }

    @PostMapping("/mo-receiver")
    public ResponseEntity<SMSReceiverResponse> smsReceiver(@Validated @RequestBody SMSReceiverRequest smsReceiver) {
        String senderAddress = smsReceiver.getSourceAddress();
        String message = smsReceiver.getMessage();

        if (senderAddress == null) {
            return new ResponseEntity<>(new SMSReceiverResponse(), HttpStatus.BAD_REQUEST);
        }

        try {
            messageUtils.checkValidityOfMessage(message, senderAddress);
            String messageContent = message.substring(4);

            switch (messageUtils.getRequestType(messageContent)) {
                case REGISTER -> smsService.registerUser(senderAddress);
                case QUESTION -> smsService.sendQuestion(messageContent, senderAddress);
                case ANSWER -> smsService.validateAnswerAndSendReply(messageContent, senderAddress);
                case SCORE -> smsService.sendScore(senderAddress);
                case LEADERBOARD -> smsService.sendLeaderboard(senderAddress);
                default -> smsService.sendInvalidRequest(senderAddress);
            }
            return new ResponseEntity<>(new SMSReceiverResponse(), HttpStatus.OK);
        } catch (UserAlreadyExistsException | QuestionNotFoundException | UserNotFoundException |
                 NoAttemptsLeftException | NotRequestedYetException | InvalidRequestException |
                 AlreadyAnsweredCorrectlyException e) {
            return new ResponseEntity<>(new SMSReceiverResponse(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
