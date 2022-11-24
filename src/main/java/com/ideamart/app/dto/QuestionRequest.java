package com.ideamart.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionRequest {
    private String sentence;
    private Map<Integer, String> answers;
    private int answer;
}
