package com.ideamart.app.model;

import com.ideamart.app.constant.QuestionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResult {
    private String id;
    private List<Attempt> attempts = new ArrayList<>();
    private QuestionStatus questionStatus;

    public QuestionResult(String id, QuestionStatus questionStatus) {
        this.id = id;
        this.questionStatus = questionStatus;
    }
}
