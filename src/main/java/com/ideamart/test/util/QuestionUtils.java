package com.ideamart.test.util;

import com.ideamart.test.model.Question;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class QuestionUtils {
    public String getQuestionString(Question question) {
        String sentence = question.getSentence();
        Map<Integer, String> answersMap = question.getAnswers();
        String answers = "";
        for (Map.Entry<Integer, String> entry : answersMap.entrySet()) {
            answers = answers.concat(entry.getKey() + ". " + entry.getValue() + "\n");

        }
        return sentence + "\n\n" + answers;
    }
}
