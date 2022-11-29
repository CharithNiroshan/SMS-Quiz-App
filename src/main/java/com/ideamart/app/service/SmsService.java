package com.ideamart.app.service;

import com.ideamart.app.constants.AnswerStatus;
import com.ideamart.app.constants.Message;
import com.ideamart.app.constants.SmsReceiverResponseCode;
import com.ideamart.app.dto.SMSReceiverResponse;
import com.ideamart.app.model.Question;
import com.ideamart.app.model.User;
import com.ideamart.app.util.MessageUtils;
import com.ideamart.app.util.QuestionUtils;
import com.ideamart.app.utilclasses.Attempt;
import com.ideamart.app.utilclasses.QuestionResult;
import com.ideamart.app.utilclasses.UserScore;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
public class SmsService {
    private final UserService userService;
    private final QuestionService questionService;
    private final MessageUtils messageUtils;
    private final QuestionUtils questionUtils;

    @Autowired
    public SmsService(UserService userService, QuestionService questionService, MessageUtils messageUtils, QuestionUtils questionUtils) {
        this.userService = userService;
        this.questionService = questionService;
        this.messageUtils = messageUtils;
        this.questionUtils = questionUtils;
    }

    public SMSReceiverResponse registerUser(String address) {
        userService.checkIfUserAlreadyExists(address);
        User user = new User(address);
        userService.saveUser(user);
        messageUtils.sendMessage(Message.REGISTERDSUCCESSFULLY.toString(), address);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0000, Message.REGISTERDSUCCESSFULLY.toString());
    }

    public SMSReceiverResponse sendQuestion(String message, String address) {
        int questionNo = messageUtils.retrieveQuestionNo(message);
        Question question = questionService.checkIfQuestionExists(questionNo, address);
        User user = userService.checkIfUserExists(address);
        if (userService.checkIfQuestionAlreadyRequestedByUser(questionNo, user.getQuestionResults())) {
            List<QuestionResult> updatedQuestionResultsList = userService.addQuestionToUserQuestionsList(user.getQuestionResults(), questionNo);
            userService.updateUserQuestionList(address, updatedQuestionResultsList);
        }
        String questionString = questionUtils.getQuestionString(question);
        messageUtils.sendMessage(questionString, address);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0001, Message.QUESTIONSENDSUCCESSFULLY.toString());
    }

    public SMSReceiverResponse validateAnswerAndSendReply(String messageContent, String address) {
        int questionNo = messageUtils.retrieveQuestionNo(messageContent);
        int answerNo = messageUtils.retrieveAnswerNo(messageContent);
        User user = userService.checkIfUserExists(address);
        Question question = questionService.checkIfQuestionExists(questionNo, address);
        QuestionResult questionResult = userService.checkIfUserHasRequestedQuestion(user.getQuestionResults(), questionNo, address);
        userService.checkForEligibilityToAnswer(questionResult, address);
        Attempt attempt;
        if (userService.checkAnswer(question.getAnswerNo(), answerNo)) {
            attempt = new Attempt(answerNo, AnswerStatus.CORRECT);
            messageUtils.sendMessage(Message.CORRECTANSWER.toString(), address);
        } else {
            attempt = new Attempt(answerNo, AnswerStatus.WRONG);
            messageUtils.sendMessage(Message.WRONGANSWER.toString(), address);
        }
        List<QuestionResult> updatedQuestionResultsList = userService.getUpdatedQuestionResultsList(user.getQuestionResults(), questionNo, attempt);
        userService.updateUserQuestionList(address, updatedQuestionResultsList);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0002, Message.ANSWERSTATUSSENDSUCCESSFULLY.toString());
    }

    public SMSReceiverResponse sendScore(String address) {
        User user = userService.checkIfUserExists(address);
        int finalScore = questionUtils.getUserFinalScore(user.getQuestionResults());
        double average = questionUtils.getUserAverage(user.getQuestionResults(), finalScore, address);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String userScoreString = "Your current score is: " + finalScore + "\nYour current average: " + Double.valueOf(decimalFormat.format(average)) + "%";
        messageUtils.sendMessage(userScoreString, address);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0003, Message.SCORESENDSUCCESSFULLY.toString());
    }

    public SMSReceiverResponse sendLeaderboard(String address) {
        List<User> users = userService.getAllUsers();
        List<UserScore> userScores = new java.util.ArrayList<>(users.stream().map(user -> {
            int finalScore = questionUtils.getUserFinalScore(user.getQuestionResults());
            if (Objects.equals(address, user.getAddress())) {
                return new UserScore("You", finalScore);
            } else {
                return new UserScore(user.getAddress(), finalScore);
            }
        }).toList());
        Collections.sort(userScores);
        String leaderboard = questionUtils.getLeaderboardString(userScores);
        messageUtils.sendMessage(leaderboard, address);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0004, Message.LEADERBOARDSENDSUCCESSFULLY.toString());
    }
}

