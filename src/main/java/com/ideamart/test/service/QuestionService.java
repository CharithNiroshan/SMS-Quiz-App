package com.ideamart.test.service;

import com.ideamart.test.dto.QuestionRequest;
import com.ideamart.test.model.Question;
import com.ideamart.test.repository.QuestionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class QuestionService {
    private final QuestionRepository questionRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository) {
        this.questionRepository = questionRepository;
    }

    public Question saveQuestion(QuestionRequest questionRequest) {
        List<Question> questionList = questionRepository.findAll();
        Question question = new Question(questionList.size() + 1, questionRequest.getSentence(), questionRequest.getAnswers(), questionRequest.getAnswer());
        return questionRepository.save(question);
    }
}
