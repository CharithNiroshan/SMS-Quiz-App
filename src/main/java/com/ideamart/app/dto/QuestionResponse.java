package com.ideamart.app.dto;

import com.ideamart.app.entity.Question;
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
