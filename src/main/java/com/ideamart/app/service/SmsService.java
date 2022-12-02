package com.ideamart.app.service;

import com.ideamart.app.constant.AnswerStatus;
import com.ideamart.app.constant.Message;
import com.ideamart.app.constant.SmsReceiverResponseCode;
import com.ideamart.app.dto.SMSReceiverResponse;
import com.ideamart.app.entity.Question;
import com.ideamart.app.entity.User;
import com.ideamart.app.util.MessageUtils;
import com.ideamart.app.util.QuestionUtils;
import com.ideamart.app.model.Attempt;
import com.ideamart.app.model.QuestionResult;
import com.ideamart.app.model.UserScore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

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
        userService.saveUser(address);
        messageUtils.sendMessage(Message.REGISTERED_SUCCESSFULLY.toString(), address);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0000, Message.REGISTERED_SUCCESSFULLY.toString());
    }

    public SMSReceiverResponse sendQuestion(String message, String address) {
        User user = userService.checkIfValidUser(address);
        int questionNo = messageUtils.retrieveQuestionNo(message);
        Question question = questionService.checkIfQuestionExists(questionNo, address);
        if (userService.checkIfQuestionAlreadyRequestedByUser(question.getId(), user.getQuestionResults())) {
            List<QuestionResult> updatedQuestionResultsList = userService.addQuestionToUserQuestionsList(user.getQuestionResults(), question.getId());
            userService.updateUserQuestionList(address, updatedQuestionResultsList);
        }
        String questionString = questionUtils.getQuestionString(question);
        messageUtils.sendMessage(questionString, address);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0001, Message.QUESTION_SEND_SUCCESSFULLY.toString());
    }

    public SMSReceiverResponse validateAnswerAndSendReply(String message, String address) {
        User user = userService.checkIfValidUser(address);
        int questionNo = messageUtils.retrieveQuestionNo(message);
        int answerNo = messageUtils.retrieveAnswerNo(message);
        Question question = questionService.checkIfQuestionExists(questionNo, address);
        QuestionResult questionResult = userService.checkIfUserHasRequestedQuestion(user.getQuestionResults(), question.getId(), address);
        userService.checkForEligibilityToAnswer(questionResult, address);
        Attempt attempt;
        if (userService.checkAnswer(question.getAnswerNo(), answerNo)) {
            attempt = new Attempt(answerNo, AnswerStatus.CORRECT);
            messageUtils.sendMessage(Message.CORRECT_ANSWER.toString(), address);
        } else {
            attempt = new Attempt(answerNo, AnswerStatus.WRONG);
            messageUtils.sendMessage(Message.WRONG_ANSWER.toString(), address);
        }
        List<QuestionResult> updatedQuestionResultsList = userService.getUpdatedQuestionResultsList(user.getQuestionResults(), question.getId(), attempt);
        userService.updateUserQuestionList(address, updatedQuestionResultsList);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0002, Message.ANSWER_STATUS_SEND_SUCCESSFULLY.toString());
    }

    public SMSReceiverResponse sendScore(String address) {
        User user = userService.checkIfValidUser(address);
        int finalScore = questionUtils.getUserFinalScore(user.getQuestionResults());
        double average = questionUtils.getUserAverage(user.getQuestionResults(), finalScore, address);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String userScoreString = "Your current score is: " + finalScore + "\nYour current average: " + Double.valueOf(decimalFormat.format(average)) + "%";
        messageUtils.sendMessage(userScoreString, address);
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0003, Message.SCORE_SEND_SUCCESSFULLY.toString());
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
        return new SMSReceiverResponse(SmsReceiverResponseCode.S0004, Message.LEADERBOARD_SEND_SUCCESSFULLY.toString());
    }
}

