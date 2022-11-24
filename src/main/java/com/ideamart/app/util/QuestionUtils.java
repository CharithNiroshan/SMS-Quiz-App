package com.ideamart.app.util;

import com.ideamart.app.constants.Message;
import com.ideamart.app.constants.QuestionStatus;
import com.ideamart.app.model.Question;
import com.ideamart.app.utilclasses.QuestionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;

@Slf4j
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

    public String getUserScoreString(List<QuestionResult> questionResults) {
        int finalScore = 0;
        int noOfAnsweredQuestion = 0;
        for (QuestionResult questionResult : questionResults) {
            if (questionResult.getQuestionStatus() == QuestionStatus.CORRECT) {
                finalScore += 10;
            }
            if (questionResult.getQuestionStatus() != QuestionStatus.PENDING) {
                noOfAnsweredQuestion++;
            }
        }

        if (noOfAnsweredQuestion == 0) {
            return Message.NOTANSWEREDYET.toString();
        } else {
            double average = (finalScore * 100) / (noOfAnsweredQuestion * 10);
            DecimalFormat decimalFormat=new DecimalFormat("#.##");
            return "Your current score is: " + finalScore + "\nYour current average: " + Double.valueOf(decimalFormat.format(average) )+ "%";
        }
    }
}
