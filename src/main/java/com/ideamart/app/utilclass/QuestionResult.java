package com.ideamart.app.utilclass;

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
    private int questionNo;
    private List<Attempt> attempts = new ArrayList<>();
    private QuestionStatus questionStatus;

    public QuestionResult(int questionNo, QuestionStatus questionStatus) {
        this.questionNo = questionNo;
        this.questionStatus = questionStatus;
    }
}
