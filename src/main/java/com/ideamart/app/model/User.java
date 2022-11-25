package com.ideamart.app.model;

import com.ideamart.app.utilclasses.QuestionResult;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document("users")
public class User {
    @Id
    private String id;
    private String address;
    private List<QuestionResult> questionResults = new ArrayList<>();

    public User(String address) {
        this.address = address;
    }
}
