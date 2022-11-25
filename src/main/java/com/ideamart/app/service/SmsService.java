package com.ideamart.app.service;

import com.ideamart.app.constants.AnswerStatus;
import com.ideamart.app.constants.Message;
import com.ideamart.app.model.Question;
import com.ideamart.app.model.User;
import com.ideamart.app.util.MessageUtils;
import com.ideamart.app.util.QuestionUtils;
import com.ideamart.app.utilclasses.Attempt;
import com.ideamart.app.utilclasses.QuestionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.List;

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

    public void registerUser(String address) {
        userService.checkIfUserAlreadyExists(address);
        User user = new User(address);
        userService.saveUser(user);
        messageUtils.sendMessage(Message.REGISTERDSUCCESSFULLY.toString(), address);
    }

    public void sendQuestion(String messageContent, String address) {
        int questionNo = messageUtils.retrieveQuestionNo(messageContent);
        Question question = questionService.checkIfQuestionExists(questionNo, address);
        User user = userService.checkIfUserExists(address);
        if (userService.checkIfQuestionAlreadyRequestedByUser(questionNo, user.getQuestionResults())) {
            List<QuestionResult> updatedQuestionResultsList = userService.addQuestionToUserQuestionsList(user.getQuestionResults(), questionNo);
            userService.updateUserQuestionList(address, updatedQuestionResultsList);
        }
        String questionString = questionUtils.getQuestionString(question);
        messageUtils.sendMessage(questionString, address);
    }

    public void validateAnswerAndSendReply(String messageContent, String address) {
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
    }

    public void sendScore(String address) {
        User user = userService.checkIfUserExists(address);
        int finalScore = questionUtils.getUserFinalScore(user.getQuestionResults());
        double average = questionUtils.getUserAverage(user.getQuestionResults(), finalScore, address);
        DecimalFormat decimalFormat = new DecimalFormat("#.##");
        String userScoreString = "Your current score is: " + finalScore + "\nYour current average: " + Double.valueOf(decimalFormat.format(average)) + "%";
        messageUtils.sendMessage(userScoreString, address);
    }

    public void sendLeaderboard(String address) {
        List<User> users = userService.getAllUsers();
        List<Integer> scores = users.stream().map(user -> questionUtils.getUserFinalScore(user.getQuestionResults())).toList();
        String leaderboard = questionUtils.getLeaderboardString(users, scores);
        messageUtils.sendMessage(leaderboard, address);
    }

    public void sendInvalidRequest(String address) {
        messageUtils.sendMessage(Message.INVALIDREQUEST.toString(), address);
    }
}

