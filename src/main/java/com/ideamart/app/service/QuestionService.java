package com.ideamart.app.service;

import com.ideamart.app.constant.Message;
import com.ideamart.app.dto.QuestionRequest;
import com.ideamart.app.exception.QuestionNotFoundException;
import com.ideamart.app.entity.Question;
import com.ideamart.app.repository.QuestionRepository;
import com.ideamart.app.util.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final MessageUtils messageUtils;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, MessageUtils messageUtils) {
        this.questionRepository = questionRepository;
        this.messageUtils = messageUtils;
    }

    public Question saveQuestion(QuestionRequest questionRequest) {
        Question question = new Question(questionRequest.getSentence(), questionRequest.getAnswers(), questionRequest.getAnswer());
        return questionRepository.save(question);
    }

    public Question checkIfQuestionExists(int questionNo, String address) {
        List<Question> questions = questionRepository.findAll();

        if (questionNo > questions.size()) {
            messageUtils.sendMessage(Message.QUESTION_NOT_FOUND.toString(), address);
            throw new QuestionNotFoundException();
        } else {
            return questions.get(questionNo - 1);
        }
    }
}
