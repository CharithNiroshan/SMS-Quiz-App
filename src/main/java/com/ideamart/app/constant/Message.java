package com.ideamart.app.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Message {
    QUESTION_NOT_FOUND("Invalid question number."),
    USER_NOT_FOUND("You are not registered with us. Send TEST REG to 77000 to register."),
    USER_ALREADY_EXISTS("You are already registered."),
    CORRECT_ANSWER("Congratulations. Your answer is correct."),
    WRONG_ANSWER("Sorry. Your answer is wrong."),
    NOT_ANSWERED_YET("You haven't answered to any questions yet."),
    NO_ATTEMPTS_LEFT("You don't have anymore attempts left to answer this question."),
    NOT_REQUESTED_YET("You haven't requested the question yet."),
    INVALID_REQUEST("Invalid request."),
    ALREADY_ANSWERED_CORRECTLY("You have already answered correctly to this question. Try another question."),
    REGISTERED_SUCCESSFULLY("Registration successful."),
    QUESTION_SEND_SUCCESSFULLY("Question send successfully."),
    SCORE_SEND_SUCCESSFULLY("User score send successfully."),
    LEADERBOARD_SEND_SUCCESSFULLY("Leaderboard send successfully."),
    ANSWER_STATUS_SEND_SUCCESSFULLY("Answer status send successfully to user.");

    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
