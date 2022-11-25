package com.ideamart.app.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Message {
    QUESTIONNOTFOUND("Invalid question number."),
    USERNOTFOUND("You are not registered with us. Send TEST REG to 77000 to register."),
    USERALREADYEXISTS("You are already registered."),
    REGISTERDSUCCESSFULLY("Registration successful."),
    CORRECTANSWER("Congratulations. Your answer is correct."),
    WRONGANSWER("Sorry. Your answer is wrong."),
    NOTANSWEREDYET("You haven't answered to any questions yet."),
    NOATTEMPTSLEFT("You don't have anymore attempts left to answer this question."),
    NOTREQUESTEDYET("You haven't requested the question yet."),
    INVALIDREQUEST("Invalid request."),
    EMPTYMESSAGE("Invalid request. Message is empty."),
    NOTCONTAINSTEST("Invalid request. Message is doesn't contain TEST."),
    NOTSTARTSWITHTEST("Invalid request. Message doesn't starts with TEST."),
    ALREADYANSWEREDCORRECTLY("You have already answered correctly to this question. Try another question."),
    ;

    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
