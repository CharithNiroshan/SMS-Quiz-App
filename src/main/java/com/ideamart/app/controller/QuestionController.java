package com.ideamart.app.controller;

import com.ideamart.app.dto.QuestionRequest;
import com.ideamart.app.dto.QuestionResponse;
import com.ideamart.app.model.Question;
import com.ideamart.app.service.QuestionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

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
}

