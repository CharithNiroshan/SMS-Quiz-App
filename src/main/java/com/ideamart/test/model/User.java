package com.ideamart.test.model;

import com.ideamart.test.utilclasses.QuestionResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class User {
    @Id
    private String id;
    private String address;
    private List<QuestionResult> questionResults;

    public User(String address, List<QuestionResult> questionResults) {
        this.address = address;
        this.questionResults = questionResults;
    }
}
