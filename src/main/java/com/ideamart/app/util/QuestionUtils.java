package com.ideamart.app.util;

import com.ideamart.app.constants.AnswerStatus;
import com.ideamart.app.constants.Message;
import com.ideamart.app.constants.QuestionStatus;
import com.ideamart.app.exception.NotAnsweredYetException;
import com.ideamart.app.model.Question;
import com.ideamart.app.utilclasses.Attempt;
import com.ideamart.app.utilclasses.QuestionResult;
import com.ideamart.app.utilclasses.UserScore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class QuestionUtils {
    private final MessageUtils messageUtils;

    @Autowired
    public QuestionUtils(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public String getQuestionString(Question question) {
        String sentence = question.getSentence();
        Map<Integer, String> answersMap = question.getAnswers();
        String answers = "";
        for (Map.Entry<Integer, String> entry : answersMap.entrySet()) {
            answers = answers.concat(entry.getKey() + ". " + entry.getValue() + "\n");

        }
        return sentence + "\n\n" + answers;
    }

    public int getUserFinalScore(List<QuestionResult> questionResults) {
        int finalScore = 0;

        for (QuestionResult questionResult : questionResults) {
            for (Attempt attempt : questionResult.getAttempts()) {
                if (attempt.getAnswerStatus() == AnswerStatus.CORRECT && questionResult.getAttempts().indexOf(attempt) == 0) {
                    finalScore += 10;
                } else if (attempt.getAnswerStatus() == AnswerStatus.CORRECT && questionResult.getAttempts().indexOf(attempt) == 1) {
                    finalScore += 5;
                }
            }
        }

        return finalScore;
    }

    public double getUserAverage(List<QuestionResult> questionResults, int finalScore, String address) {
        int noOfAnsweredQuestion = 0;

        for (QuestionResult questionResult : questionResults) {
            if (questionResult.getQuestionStatus() == QuestionStatus.ANSWERED) {
                noOfAnsweredQuestion++;
            }
        }

        if (noOfAnsweredQuestion == 0) {
            messageUtils.sendMessage(Message.NOTANSWEREDYET.toString(), address);
            throw new NotAnsweredYetException();
        } else {
            return (finalScore * 100) / (noOfAnsweredQuestion * 10);
        }
    }

    public String getLeaderboardString(List<UserScore> userScores) {
        StringBuilder leaderboard = new StringBuilder();
        leaderboard.append("\n\nLEADERBOARD\n\n");
        userScores.forEach(userScore -> leaderboard.append(userScore.getAddress()).append(" : ").append(userScore.getScore()).append("\n"));
        return leaderboard.toString();
    }
}

