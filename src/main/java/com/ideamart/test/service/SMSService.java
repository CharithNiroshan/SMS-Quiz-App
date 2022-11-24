package com.ideamart.test.service;

import com.ideamart.test.dto.SMSSenderRequest;
import com.ideamart.test.dto.SMSSenderResponse;
import com.ideamart.test.exception.QuestionNotFoundException;
import com.ideamart.test.model.Question;
import com.ideamart.test.repository.QuestionRepository;
import com.ideamart.test.util.QuestionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;

@Service
@Slf4j
public class SMSService {
    private static final String SERVERURL = "http://localhost:7000/sms/send";
    private static final String PASSWORD = "password";
    private final RestTemplate restTemplate = new RestTemplate();
    private final QuestionRepository questionRepository;
    private final QuestionUtils questionUtils;

    @Autowired
    public SMSService(QuestionRepository questionRepository, QuestionUtils questionUtils) {
        this.questionRepository = questionRepository;
        this.questionUtils = questionUtils;
    }

    public void sendMessage(String message, String destinationAddress, String applicationId) {
        SMSSenderRequest smsSenderRequest = new SMSSenderRequest(message, destinationAddress, applicationId, PASSWORD);
        restTemplate.postForObject(SERVERURL, smsSenderRequest, SMSSenderResponse.class);
    }

    public void sendQuestion(int questionNo, String destinationAddress, String applicationId) throws QuestionNotFoundException {
        Optional<Question> question = questionRepository.findByQuestionNo(questionNo);
        if (question.isPresent()) {
            String message = questionUtils.getQuestionString(question.get());
            SMSSenderRequest smsSenderRequest = new SMSSenderRequest(message, destinationAddress, applicationId, PASSWORD);
            restTemplate.postForObject(SERVERURL, smsSenderRequest, SMSSenderResponse.class);
        } else {
            throw new QuestionNotFoundException();
        }
    }

    public void sendAnswerValidity(int questionNo, int answerNo, String destinationAddress, String applicationId) throws QuestionNotFoundException {
        Optional<Question> question = questionRepository.findByQuestionNo(questionNo);
        if (question.isPresent()) {
            if (question.get().getAnswerNo() == answerNo) {
                SMSSenderRequest smsSenderRequest = new SMSSenderRequest("Answer is Correct", destinationAddress, applicationId, PASSWORD);
                restTemplate.postForObject(SERVERURL, smsSenderRequest, SMSSenderResponse.class);
            } else {
                SMSSenderRequest smsSenderRequest = new SMSSenderRequest("Answer is Wrong", destinationAddress, applicationId, PASSWORD);
                restTemplate.postForObject(SERVERURL, smsSenderRequest, SMSSenderResponse.class);
            }
        } else {
            throw new QuestionNotFoundException();
        }
    }
}
