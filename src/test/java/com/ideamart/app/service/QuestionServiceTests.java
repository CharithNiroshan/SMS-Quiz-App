package com.ideamart.app.service;

import com.ideamart.app.dto.QuestionRequest;
import com.ideamart.app.exception.QuestionNotFoundException;
import com.ideamart.app.entity.Question;
import com.ideamart.app.repository.QuestionRepository;
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
class QuestionServiceTests {
    @InjectMocks
    private QuestionService questionService;

    @Mock
    private QuestionRepository questionRepository;
    @Mock
    private MessageUtils messageUtils;

    private final List<Question> questions = new ArrayList<>();

    private Question question;

    @BeforeEach
    public void init() {
        String sentence = "Who is Prime Minister of Sri Lanka?";
        int answerNo = 4;
        Map<Integer, String> answers = new HashMap<>();
        answers.put(1, "Ranil Wickramasinghe");
        answers.put(2, "Sajith Premadasa");
        answers.put(3, "Maithripala Sirisena");
        answers.put(4, "Dinesh Gunawardena");
        question = new Question(sentence, answers, answerNo);
        questions.add(question);
    }


    @Test
    void shouldReturnAQuestionObjectWhenValidQuestionNumberReceived() {
        given(questionRepository.findAll()).willReturn(questions);
        Question retrievedQuestion = questionService.checkIfQuestionExists(1, "tel:776213875");
        assertNotNull(retrievedQuestion);
        assertEquals(retrievedQuestion.getAnswerNo(), questions.get(0).getAnswerNo());
    }

    @Test
    void shouldThrowQuestionNotFoundExceptionWhenInvalidQuestionNoReceived() {
        given(questionRepository.findAll()).willReturn(questions);
        assertThrows(QuestionNotFoundException.class, () -> questionService.checkIfQuestionExists(8, "tel:776213875"));
    }

    @Test
    void shouldReturnCreatedQuestionWhenReceivedQuestionRequest() {
        QuestionRequest questionRequest = new QuestionRequest(question.getSentence(), question.getAnswers(), question.getAnswerNo());
        given(questionRepository.save(question)).willReturn(question);
        Question question = questionService.saveQuestion(questionRequest);
        assertEquals(question.getAnswerNo(), 4);
        assertEquals(question.getSentence(), "Who is Prime Minister of Sri Lanka?");
    }

    @Test
    void whenQuestionAddedAfterDeletingAOneDuplicateQuestionNotGenerated() {
        QuestionRequest questionRequest = new QuestionRequest(question.getSentence(), question.getAnswers(), question.getAnswerNo());
        given(questionRepository.save(question)).willReturn(question);
        Question question = questionService.saveQuestion(questionRequest);
        assertEquals(question.getAnswerNo(), 4);
        assertEquals(question.getSentence(), "Who is Prime Minister of Sri Lanka?");
    }
}
