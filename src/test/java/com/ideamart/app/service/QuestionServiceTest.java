package com.ideamart.app.service;

import com.ideamart.app.dto.QuestionRequest;
import com.ideamart.app.exception.QuestionNotFoundException;
import com.ideamart.app.model.Question;
import com.ideamart.app.repository.QuestionRepository;
import com.ideamart.app.service.QuestionService;
import com.ideamart.app.util.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.mockito.BDDMockito.given;
import static org.testng.Assert.*;

@ExtendWith(MockitoExtension.class)
class QuestionServiceTest {
    @InjectMocks
    private QuestionService questionService;

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private MessageUtils messageUtils;

    private Question question;

    @BeforeEach
    public void init() {
        int questionNo = 1;
        String sentence = "Who is Prime Minister of Sri Lanka?";
        int answerNo = 4;
        Map<Integer, String> answers = new HashMap<>();
        answers.put(1, "Ranil Wickramasinghe");
        answers.put(2, "Sajith Premadasa");
        answers.put(3, "Maithripala Sirisena");
        answers.put(4, "Dinesh Gunawardena");
        question = new Question(questionNo, sentence, answers, answerNo);
    }


    @Test
    void shouldReturnAQuestionObjectWhenValidQuestionNumberReceived() {
        given(questionRepository.findByQuestionNo(1)).willReturn(Optional.of(question));
        Question retrievedQuestion = questionService.checkIfQuestionExists(1, "tel:776213875");
        assertNotNull(retrievedQuestion);
        assertEquals(retrievedQuestion.getQuestionNo(), question.getQuestionNo());
    }

    @Test
    void shouldThrowQuestionNotFoundExceptionWhenInvalidQuestionNoReceived() {
        given(questionRepository.findByQuestionNo(8)).willReturn(Optional.empty());
        assertThrows(QuestionNotFoundException.class, () -> questionService.checkIfQuestionExists(8, "tel:776213875"));
    }

    @Test
    void shouldReturnCreatedQuestionWhenReceivedQuestionRequest() {
        QuestionRequest questionRequest = new QuestionRequest(question.getSentence(), question.getAnswers(), question.getAnswerNo());
        given(questionRepository.findAll()).willReturn(new ArrayList<Question>());
        given(questionRepository.save(question)).willReturn(question);
        Question question = questionService.saveQuestion(questionRequest);
        assertEquals(question.getAnswerNo(), 4);
        assertEquals(question.getSentence(), "Who is Prime Minister of Sri Lanka?");
    }
}
