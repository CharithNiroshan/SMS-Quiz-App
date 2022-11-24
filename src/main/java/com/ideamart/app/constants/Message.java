package com.ideamart.app.constants;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum Message {
    SUCCESS("Message Send Successfully."),
    QUESTIONNOTFOUND("Question not found."),
    USERNOTFOUND("User for the address could not be found."),
    USERALREADYEXISTS("User already exists."),
    REGISTERDSUCCESSFULLY("User added to the database successfully."),
    CORRECTANSWER("Congratulations. Your answer is correct."),
    WRONGANSWER("Sorry. Your answer is wrong."),
    ADDRESSEMPTY("Sender Address is Empty"),
    NOTANSWEREDYET("You haven't answered to any any questions yet.")
    ;

    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
