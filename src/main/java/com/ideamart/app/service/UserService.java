package com.ideamart.app.service;

import com.ideamart.app.constants.AnswerStatus;
import com.ideamart.app.constants.Message;
import com.ideamart.app.constants.QuestionStatus;
import com.ideamart.app.exception.*;
import com.ideamart.app.model.User;
import com.ideamart.app.repository.UserRepository;
import com.ideamart.app.util.MessageUtils;
import com.ideamart.app.utilclasses.Attempt;
import com.ideamart.app.utilclasses.QuestionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
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

    public void saveUser(User user) {
        userRepository.save(user);
    }

    public void checkIfUserAlreadyExists(String address) {
        Optional<User> user = userRepository.findByAddress(address);

        if (user.isPresent()) {
            messageUtils.sendMessage(Message.USERALREADYEXISTS.toString(), address);
            throw new UserAlreadyExistsException();
        }
    }

    public User checkIfUserExists(String address) {
        Optional<User> user = userRepository.findByAddress(address);

        if (user.isPresent()) {
            return user.get();
        } else {
            messageUtils.sendMessage(Message.USERNOTFOUND.toString(), address);
            throw new UserNotFoundException();
        }
    }

    public List<QuestionResult> addQuestionToUserQuestionsList(List<QuestionResult> questionResults, int questionNo) {
        QuestionResult questionResult = new QuestionResult(questionNo, QuestionStatus.NOTANSWERED);
        questionResults.add(questionResult);
        return questionResults;
    }

    public QuestionResult checkIfUserHasRequestedQuestion(List<QuestionResult> questionResults, int questionNo, String address) {
        List<QuestionResult> questionResultList = questionResults.stream().filter(questionResult -> questionResult.getQuestionNo() == questionNo).toList();
        if (!questionResultList.isEmpty()) {
            return questionResultList.get(0);
        } else {
            messageUtils.sendMessage(Message.NOTREQUESTEDYET.toString(), address);
            throw new NotRequestedYetException();
        }
    }

    public void checkForEligibilityToAnswer(QuestionResult questionResult, String address) {
        if (questionResult.getAttempts().size() >= 2) {
            messageUtils.sendMessage(Message.NOATTEMPTSLEFT.toString(), address);
            throw new NoAttemptsLeftException();
        }
        if (!questionResult.getAttempts().isEmpty() && questionResult.getAttempts().get(0).getAnswerStatus() == AnswerStatus.CORRECT) {
            messageUtils.sendMessage(Message.ALREADYANSWEREDCORRECTLY.toString(), address);
            throw new AlreadyAnsweredCorrectlyException();
        }
    }

    public List<QuestionResult> getUpdatedQuestionResultsList(List<QuestionResult> questionResults, int questionNo, Attempt attempt) {
        for (QuestionResult questionResult : questionResults) {
            if (questionResult.getQuestionNo() == questionNo) {
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

    public boolean checkIfQuestionAlreadyRequestedByUser(int questionNo, List<QuestionResult> questionResults) {
        List<QuestionResult> filteredQuestionResults = questionResults.stream().filter(question -> question.getQuestionNo() == questionNo).toList();
        return filteredQuestionResults.isEmpty();
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}
