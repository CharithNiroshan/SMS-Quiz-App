package com.ideamart.app.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Message {
    QUESTIONNOTFOUND("Invalid question number."),
    USERNOTFOUND("You are not registered with us. Send TEST REG to 77000 to register."),
    USERALREADYEXISTS("You are already registered."),
    CORRECTANSWER("Congratulations. Your answer is correct."),
    WRONGANSWER("Sorry. Your answer is wrong."),
    NOTANSWEREDYET("You haven't answered to any questions yet."),
    NOATTEMPTSLEFT("You don't have anymore attempts left to answer this question."),
    NOTREQUESTEDYET("You haven't requested the question yet."),
    INVALIDREQUEST("Invalid request."),
    ALREADYANSWEREDCORRECTLY("You have already answered correctly to this question. Try another question."),
    REGISTERDSUCCESSFULLY("Registration successful."),
    QUESTIONSENDSUCCESSFULLY("Question send successfully."),
    SCORESENDSUCCESSFULLY("User score send successfully."),
    LEADERBOARDSENDSUCCESSFULLY("Leaderboard send successfully."),
    ANSWERSTATUSSENDSUCCESSFULLY("Answer status send successfully to user.");

    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
