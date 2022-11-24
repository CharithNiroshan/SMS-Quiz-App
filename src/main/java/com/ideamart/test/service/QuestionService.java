package com.ideamart.test.service;

import com.ideamart.test.constants.Message;
import com.ideamart.test.constants.QuestionStatus;
import com.ideamart.test.dto.QuestionRequest;
import com.ideamart.test.exception.QuestionNotFoundException;
import com.ideamart.test.model.Question;
import com.ideamart.test.repository.QuestionRepository;
import com.ideamart.test.util.MessageUtils;
import com.ideamart.test.util.QuestionUtils;
import com.ideamart.test.utilclasses.QuestionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class QuestionService {
    private final QuestionRepository questionRepository;
    private final UserService userService;
    private final MessageUtils messageUtils;
    private final QuestionUtils questionUtils;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, UserService userService, MessageUtils messageUtils, QuestionUtils questionUtils) {
        this.questionRepository = questionRepository;
        this.userService = userService;
        this.messageUtils = messageUtils;
        this.questionUtils = questionUtils;
    }

    public Question saveQuestion(QuestionRequest questionRequest) {
        List<Question> questionList = questionRepository.findAll();
        Question question = new Question(questionList.size() + 1, questionRequest.getSentence(), questionRequest.getAnswers(), questionRequest.getAnswer());
        return questionRepository.save(question);
    }

    public void sendQuestion(int questionNo, String destinationAddress) {
        Optional<Question> question = questionRepository.findByQuestionNo(questionNo);

        if (question.isPresent()) {
            QuestionResult questionResult = new QuestionResult(questionNo, QuestionStatus.PENDING);
            userService.addQuestionToUser(destinationAddress, questionResult);
            messageUtils.sendMessage(questionUtils.getQuestionString(question.get()), destinationAddress);
        } else {
            messageUtils.sendMessage(Message.QUESTIONNOTFOUND.toString(), destinationAddress);
            throw new QuestionNotFoundException();
        }
    }

    public void checkAnswer(int questionNo, int answerNo, String destinationAddress) {
        Optional<Question> question = questionRepository.findByQuestionNo(questionNo);

        if (question.isPresent()) {
            if (question.get().getAnswerNo() == answerNo) {
                userService.updateQuestionResultAfterAnswer(destinationAddress, questionNo, answerNo, QuestionStatus.CORRECT);
                messageUtils.sendMessage(Message.CORRECTANSWER.toString(), destinationAddress);
            } else {
                userService.updateQuestionResultAfterAnswer(destinationAddress, questionNo, answerNo, QuestionStatus.WRONG);
                messageUtils.sendMessage(Message.WRONGANSWER.toString(), destinationAddress);
            }
        } else {
            messageUtils.sendMessage(Message.QUESTIONNOTFOUND.toString(), destinationAddress);
            throw new QuestionNotFoundException();
        }
    }
}
