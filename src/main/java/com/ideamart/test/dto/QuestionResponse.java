package com.ideamart.test.dto;

import com.ideamart.test.model.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResponse {
    private String message;
    private Question question;
}
