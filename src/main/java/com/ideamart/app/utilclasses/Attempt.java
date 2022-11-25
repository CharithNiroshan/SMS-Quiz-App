package com.ideamart.app.utilclasses;

import com.ideamart.app.constants.AnswerStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Attempt {
    private int answerGiven;
    private AnswerStatus answerStatus;
}