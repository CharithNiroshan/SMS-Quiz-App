package com.ideamart.app.controller;

import com.ideamart.app.constant.Message;
import com.ideamart.app.constant.SmsReceiverResponseCode;
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
            SMSReceiverResponse smsReceiverResponse;

            switch (messageUtils.getRequestType(message, senderAddress)) {
                case REGISTER -> smsReceiverResponse = smsService.registerUser(senderAddress);
                case QUESTION -> smsReceiverResponse = smsService.sendQuestion(message, senderAddress);
                case ANSWER -> smsReceiverResponse = smsService.validateAnswerAndSendReply(message, senderAddress);
                case SCORE -> smsReceiverResponse = smsService.sendScore(senderAddress);
                case LEADERBOARD -> smsReceiverResponse = smsService.sendLeaderboard(senderAddress);
                default -> throw new InvalidRequestException();
            }

            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (InvalidRequestException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0000, Message.INVALID_REQUEST.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0001, Message.USER_ALREADY_EXISTS.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (UserNotFoundException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0002, Message.USER_NOT_FOUND.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (QuestionNotFoundException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0003, Message.QUESTION_NOT_FOUND.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (NotRequestedYetException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0004, Message.NOT_REQUESTED_YET.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (NoAttemptsLeftException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0005, Message.NO_ATTEMPTS_LEFT.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (AlreadyAnsweredCorrectlyException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0006, Message.ALREADY_ANSWERED_CORRECTLY.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (NotAnsweredYetException e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E0007, Message.NOT_ANSWERED_YET.toString());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.OK);
        } catch (Exception e) {
            SMSReceiverResponse smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.E1000, e.getMessage());
            return new ResponseEntity<>(smsReceiverResponse, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
