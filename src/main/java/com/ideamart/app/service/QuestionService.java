package com.ideamart.app.service;

import com.ideamart.app.constants.Message;
import com.ideamart.app.dto.QuestionRequest;
import com.ideamart.app.exception.QuestionNotFoundException;
import com.ideamart.app.model.Question;
import com.ideamart.app.repository.QuestionRepository;
import com.ideamart.app.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MessageUtils messageUtils;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, MessageUtils messageUtils) {
        this.questionRepository = questionRepository;
        this.messageUtils = messageUtils;
    }

    public Question saveQuestion(QuestionRequest questionRequest) {
        List<Question> questionList = questionRepository.findAll();
        Question question = new Question(questionList.size() + 1, questionRequest.getSentence(), questionRequest.getAnswers(), questionRequest.getAnswer());
        return questionRepository.save(question);
    }

    public Question checkIfQuestionExists(int questionNo, String address) {
        Optional<Question> question = questionRepository.findByQuestionNo(questionNo);

        if (question.isEmpty()) {
            messageUtils.sendMessage(Message.QUESTIONNOTFOUND.toString(), address);
            throw new QuestionNotFoundException();
        } else {
            return question.get();
        }
    }
}
