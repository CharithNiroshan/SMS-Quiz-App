package com.ideamart.test.controller;

import com.ideamart.test.constants.Message;
import com.ideamart.test.dto.QuestionRequest;
import com.ideamart.test.dto.QuestionResponse;
import com.ideamart.test.dto.SMSReceiverRequest;
import com.ideamart.test.exception.QuestionNotFoundException;
import com.ideamart.test.exception.UserNotFoundException;
import com.ideamart.test.model.Question;
import com.ideamart.test.service.QuestionService;
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
@RequestMapping("/api/question")
@Controller
public class QuestionController {
    private final QuestionService questionService;

    @Autowired
    public QuestionController(QuestionService questionService) {
        this.questionService = questionService;
    }

    @PostMapping("")
    public ResponseEntity<QuestionResponse> addQuestion(@Validated @RequestBody QuestionRequest questionRequest) {
        if (questionRequest.getSentence() == null || questionRequest.getAnswers() == null || questionRequest.getAnswer() == 0) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        try {
            Question question = questionService.saveQuestion(questionRequest);
            QuestionResponse questionResponse = new QuestionResponse("Question Successfully Added", question);
            return new ResponseEntity<>(questionResponse, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/get-question")
    public ResponseEntity<String> receiveUserMessageAndSendQuestion(@RequestParam int questionNo, @Validated @RequestBody SMSReceiverRequest smsReceiver) {
        String senderAddress = smsReceiver.getSourceAddress();

        try {
            questionService.sendQuestion(questionNo, senderAddress);
            return new ResponseEntity<>(Message.SUCCESS.toString(), HttpStatus.OK);
        } catch (QuestionNotFoundException e) {
            return new ResponseEntity<>(Message.QUESTIONNOTFOUND.toString(), HttpStatus.NOT_FOUND);
        } catch (UserNotFoundException e) {
            return new ResponseEntity<>(Message.USERNOTFOUND.toString(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/answer-question")
    public ResponseEntity<String> receiveAnswerAndCheckForValidity(@RequestParam int questionNo, @RequestParam int answerNo, @Validated @RequestBody SMSReceiverRequest smsReceiver) {
        String senderAddress = smsReceiver.getSourceAddress();

        try {
            questionService.checkAnswer(questionNo, answerNo, senderAddress);
            return new ResponseEntity<>(Message.SUCCESS.toString(), HttpStatus.OK);
        } catch (QuestionNotFoundException e) {
            return new ResponseEntity<>(Message.QUESTIONNOTFOUND.toString(), HttpStatus.NOT_FOUND);
        } catch (UserNotFoundException e){
            return new ResponseEntity<>(Message.USERNOTFOUND.toString(), HttpStatus.FORBIDDEN);
        }catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}

