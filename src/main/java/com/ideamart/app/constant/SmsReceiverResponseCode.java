package com.ideamart.app.constant;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum SmsReceiverResponseCode {
    E0000("Invalid request"),
    E0001("User already exists"),
    E0002("Invalid user"),
    E0003("Invalid question"),
    E0004("Question not requested yet"),
    E0005("No attempts left"),
    E0006("Already answered correctly"),
    E0007("Not answered yet"),
    E1000("Server error"),
    S0000("Registered successfully"),
    S0001("Question send successfully"),
    S0002("Answer status send successfully"),
    S0003("Score send successfully"),
    S0004("Leaderboard send successfully");

    private final String text;

    @Override
    public String toString() {
        return text;
    }
}
