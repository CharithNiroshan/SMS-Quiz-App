package com.ideamart.app.utilclasses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserScore implements Comparable<UserScore> {
    private String address;
    private int score;

    @Override
    public int compareTo(UserScore userScore) {
        return Integer.compare(userScore.getScore(), this.getScore());
    }
}
