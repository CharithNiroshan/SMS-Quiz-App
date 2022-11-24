package com.ideamart.test.controller;

import com.ideamart.test.dto.SMSReceiverRequest;
import com.ideamart.test.exception.QuestionNotFoundException;
import com.ideamart.test.service.SMSService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@RequestMapping("/api/sms")
@Controller
public class SMSController {
    private final SMSService smsService;
    private static final String RETURNMESSAGEWHENSUCCESS = "Message Send Successfully.";
    private static final String RETURNMESSAGEWHENNOTFOUND = "Question Not Found";

    @Autowired
    public SMSController(SMSService smsService) {
        this.smsService = smsService;
    }

    @PostMapping("/text-receiver")
    public ResponseEntity<String> receiveUserMessageAndSendMessage(@Validated @RequestBody SMSReceiverRequest smsReceiver) {
        String senderAddress = smsReceiver.getSourceAddress();
        String applicationId = smsReceiver.getApplicationId();

        try {
            smsService.sendMessage("Successfully received your message.", senderAddress, applicationId);
            return new ResponseEntity<>(RETURNMESSAGEWHENSUCCESS, HttpStatus.OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/question-receiver")
    public ResponseEntity<String> receiveUserMessageAndSendQuestion(@RequestParam int questionNo, @Validated @RequestBody SMSReceiverRequest smsReceiver) {
        String senderAddress = smsReceiver.getSourceAddress();
        String applicationId = smsReceiver.getApplicationId();

        try {
            smsService.sendQuestion(questionNo, senderAddress, applicationId);
            return new ResponseEntity<>(RETURNMESSAGEWHENSUCCESS, HttpStatus.OK);
        } catch (QuestionNotFoundException e) {
            return new ResponseEntity<>(RETURNMESSAGEWHENNOTFOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/answer-receiver")
    public ResponseEntity<String> receiveAnswerAndCheckForValidity(@RequestParam int questionNo, @RequestParam int answerNo, @Validated @RequestBody SMSReceiverRequest smsReceiver) {
        String senderAddress = smsReceiver.getSourceAddress();
        String applicationId = smsReceiver.getApplicationId();

        try {
            smsService.sendAnswerValidity(questionNo, answerNo, senderAddress, applicationId);
            return new ResponseEntity<>(RETURNMESSAGEWHENSUCCESS, HttpStatus.OK);
        } catch (QuestionNotFoundException e) {
            return new ResponseEntity<>(RETURNMESSAGEWHENNOTFOUND, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
