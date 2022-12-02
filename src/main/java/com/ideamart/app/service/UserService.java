package com.ideamart.app.service;

import com.ideamart.app.constant.AnswerStatus;
import com.ideamart.app.constant.Message;
import com.ideamart.app.constant.QuestionStatus;
import com.ideamart.app.exception.*;
import com.ideamart.app.entity.User;
import com.ideamart.app.repository.UserRepository;
import com.ideamart.app.util.MessageUtils;
import com.ideamart.app.model.Attempt;
import com.ideamart.app.model.QuestionResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final MongoTemplate mongoTemplate;
    private final MessageUtils messageUtils;

    @Autowired
    public UserService(UserRepository userRepository, MongoTemplate mongoTemplate, MessageUtils messageUtils) {
        this.userRepository = userRepository;
        this.mongoTemplate = mongoTemplate;
        this.messageUtils = messageUtils;
    }

    public void saveUser(String address) {
        User user = new User(address);
        userRepository.save(user);
    }

    public void checkIfUserAlreadyExists(String address) {
        Optional<User> user = userRepository.findByAddress(address);

        if (user.isPresent()) {
            messageUtils.sendMessage(Message.USER_ALREADY_EXISTS.toString(), address);
            throw new UserAlreadyExistsException();
        }
    }

    public User checkIfValidUser(String address) {
        Optional<User> user = userRepository.findByAddress(address);

        if (user.isPresent()) {
            return user.get();
        } else {
            messageUtils.sendMessage(Message.USER_NOT_FOUND.toString(), address);
            throw new UserNotFoundException();
        }
    }

    public List<QuestionResult> addQuestionToUserQuestionsList(List<QuestionResult> questionResults, String id) {
        QuestionResult questionResult = new QuestionResult(id, QuestionStatus.NOTANSWERED);
        questionResults.add(questionResult);
        return questionResults;
    }

    public QuestionResult checkIfUserHasRequestedQuestion(List<QuestionResult> questionResults, String id, String address) {
        List<QuestionResult> questionResultList = questionResults.stream().filter(questionResult -> Objects.equals(questionResult.getId(), id)).toList();
        if (!questionResultList.isEmpty()) {
            return questionResultList.get(0);
        } else {
            messageUtils.sendMessage(Message.NOT_REQUESTED_YET.toString(), address);
            throw new NotRequestedYetException();
        }
    }

    public void checkForEligibilityToAnswer(QuestionResult questionResult, String address) {
        if (!questionResult.getAttempts().isEmpty()) {
            for (Attempt attempt : questionResult.getAttempts()) {
                if (attempt.getAnswerStatus() == AnswerStatus.CORRECT) {
                    messageUtils.sendMessage(Message.ALREADY_ANSWERED_CORRECTLY.toString(), address);
                    throw new AlreadyAnsweredCorrectlyException();
                }
            }
        }
        if (questionResult.getAttempts().size() >= 2) {
            messageUtils.sendMessage(Message.NO_ATTEMPTS_LEFT.toString(), address);
            throw new NoAttemptsLeftException();
        }
    }

    public List<QuestionResult> getUpdatedQuestionResultsList(List<QuestionResult> questionResults, String id, Attempt attempt) {
        for (QuestionResult questionResult : questionResults) {
            if (Objects.equals(questionResult.getId(), id)) {
                questionResult.getAttempts().add(attempt);
                if (questionResult.getQuestionStatus() == QuestionStatus.NOTANSWERED) {
                    questionResult.setQuestionStatus(QuestionStatus.ANSWERED);
                }
            }
        }

        return questionResults;
    }

    public boolean checkAnswer(int answer, int userAnswer) {
        return answer == userAnswer;
    }

    public void updateUserQuestionList(String destinationAddress, List<QuestionResult> questionResults) {
        Query query = new Query().addCriteria(Criteria.where("address").is(destinationAddress));
        Update update = new Update().set("questionResults", questionResults);
        FindAndModifyOptions findAndModifyOptions = new FindAndModifyOptions().upsert(true);
        mongoTemplate.findAndModify(query, update, findAndModifyOptions, User.class);
    }

    public boolean checkIfQuestionAlreadyRequestedByUser(String questionId, List<QuestionResult> questionResults) {
        List<QuestionResult> filteredQuestionResults = questionResults.stream().filter(question -> Objects.equals(question.getId(), questionId)).toList();
        return filteredQuestionResults.isEmpty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
