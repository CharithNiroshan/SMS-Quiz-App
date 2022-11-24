package com.ideamart.test.utilclasses;

import com.ideamart.test.constants.QuestionStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResult {
    private int questionNo;
    private int answerGiven;
    private QuestionStatus questionStatus;

    public QuestionResult(int questionNo, QuestionStatus questionStatus) {
        this.questionNo = questionNo;
        this.questionStatus = questionStatus;
    }
}
