package com.ideamart.app.service;

import com.ideamart.app.constants.Message;
import com.ideamart.app.constants.QuestionStatus;
import com.ideamart.app.exception.UserAlreadyExistsException;
import com.ideamart.app.exception.UserNotFoundException;
import com.ideamart.app.model.User;
import com.ideamart.app.repository.UserRepository;
import com.ideamart.app.util.MessageUtils;
import com.ideamart.app.util.QuestionUtils;
import com.ideamart.app.utilclasses.QuestionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class UserService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final MessageUtils messageUtils;
    private final QuestionUtils questionUtils;

    @Autowired
    public UserService(UserRepository userRepository, MongoTemplate mongoTemplate, MessageUtils messageUtils, QuestionUtils questionUtils) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.messageUtils = messageUtils;
        this.questionUtils = questionUtils;
    }

    public void saveUser(String destinationAddress) {
        Optional<User> user = getUser(destinationAddress);
        List<QuestionResult> questionResults = new ArrayList<>();

        if (user.isPresent()) {
            messageUtils.sendMessage(Message.USERALREADYEXISTS.toString(), destinationAddress);
            throw new UserAlreadyExistsException();
        } else {
            User newUser = new User(destinationAddress, questionResults);
            userRepository.save(newUser);
            messageUtils.sendMessage(Message.REGISTERDSUCCESSFULLY.toString(), destinationAddress);
        }
    }

    public void addQuestionToUser(String destinationAddress, QuestionResult questionResult) {
        Optional<User> user = getUser(destinationAddress);

        if (user.isPresent()) {
            List<QuestionResult> questionResults = user.get().getQuestionResults();
            questionResults.add(questionResult);
            updateUserQuestionList(destinationAddress, questionResults);
        } else {
            messageUtils.sendMessage(Message.USERNOTFOUND.toString(), destinationAddress);
            throw new UserNotFoundException();
        }
    }

    public void updateQuestionResultAfterAnswer(String destinationAddress, int questionNo, int answerNo, QuestionStatus questionStatus) {
        Optional<User> user = getUser(destinationAddress);

        if (user.isPresent()) {
            List<QuestionResult> questionResults = user.get().getQuestionResults();
            for (QuestionResult questionResult : questionResults) {
                if (questionResult.getQuestionNo() == questionNo) {
                    questionResult.setQuestionStatus(questionStatus);
                    questionResult.setAnswerGiven(answerNo);
                }
            }
            updateUserQuestionList(destinationAddress, questionResults);
        } else {
            messageUtils.sendMessage(Message.USERNOTFOUND.toString(), destinationAddress);
            throw new UserNotFoundException();
        }
    }

    public void getUserScore(String destinationAddress) {
        Optional<User> user = getUser(destinationAddress);

        if (user.isPresent()) {
            List<QuestionResult> questionResults = user.get().getQuestionResults();
            messageUtils.sendMessage(questionUtils.getUserScoreString(questionResults), destinationAddress);
        } else {
            messageUtils.sendMessage(Message.USERNOTFOUND.toString(), destinationAddress);
            throw new UserNotFoundException();
        }
    }

    public Optional<User> getUser(String address) {
        return userRepository.findByAddress(address);
    }

    public void updateUserQuestionList(String destinationAddress, List<QuestionResult> questionResults) {
        Query query = new Query().addCriteria(Criteria.where("address").is(destinationAddress));
        Update update = new Update().set("questionResults", questionResults);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions().upsert(true);
        mongoTemplate.findAndModify(query, update, findAndModifyOptions, User.class);
    }
}
