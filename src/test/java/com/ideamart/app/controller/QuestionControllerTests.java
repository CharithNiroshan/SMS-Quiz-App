package com.ideamart.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideamart.app.dto.QuestionRequest;
import com.ideamart.app.entity.Question;
import com.ideamart.app.service.QuestionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuestionController.class)
class QuestionControllerTests {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private QuestionService questionService;

    private QuestionRequest questionRequest;

    @BeforeEach
    public void init() {
        String sentence = "Who is Prime Minister of Sri Lanka?";
        int answerNo = 4;
        Map<Integer, String> answers = new HashMap<>();
        answers.put(1, "Ranil Wickramasinghe");
        answers.put(2, "Sajith Premadasa");
        answers.put(3, "Maithripala Sirisena");
        answers.put(4, "Dinesh Gunawardena");
        questionRequest = new QuestionRequest(sentence, answers, answerNo);
    }

    @Test
    void whenPassedQuestionRequestObjectReturnsNotNullObjectOfQuestionClass() throws Exception {
        Question question = new Question(questionRequest.getSentence(), questionRequest.getAnswers(), questionRequest.getAnswer());
        given(questionService.saveQuestion(questionRequest)).willReturn(question);
        mockMvc.perform(
                        post("/api/question")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(questionRequest)
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Question Successfully Added"))
                .andExpect(jsonPath("$.question.answerNo").value(question.getAnswerNo()))
                .andExpect(jsonPath("$.question.sentence").value(question.getSentence()));
    }

    @Test
    void whenPassedAEmptyQuestionRequestObjectShouldReturn() throws Exception {
        mockMvc.perform(
                post("/api/question")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new QuestionRequest())
                        )).andExpect(status().isBadRequest());
    }
}
