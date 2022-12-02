package com.ideamart.app.entity;

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
    private String sentence;
    private Map<Integer, String> answers;
    private int answerNo;

    public Question(String sentence, Map<Integer, String> answers, int answerNo) {
        this.sentence = sentence;
        this.answers = answers;
        this.answerNo = answerNo;
    }
}
