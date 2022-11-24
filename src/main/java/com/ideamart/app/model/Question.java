package com.ideamart.app.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("questions")
public class Question {
    @Id
    private String id;
    private int questionNo;
    private String sentence;
    private Map<Integer, String> answers;
    private int answerNo;

    public Question(int questionNo, String sentence, Map<Integer, String> answers, int answerNo) {
        this.sentence = sentence;
        this.answers = answers;
        this.answerNo = answerNo;
        this.questionNo = questionNo;
    }
}