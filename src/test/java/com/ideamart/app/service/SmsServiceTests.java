package com.ideamart.app.service;

import com.ideamart.app.constants.SmsReceiverResponseCode;
import com.ideamart.app.dto.SMSReceiverResponse;
import com.ideamart.app.exception.QuestionNotFoundException;
import com.ideamart.app.exception.UserNotFoundException;
import com.ideamart.app.model.Question;
import com.ideamart.app.model.User;
import com.ideamart.app.util.MessageUtils;
import com.ideamart.app.util.QuestionUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.testng.Assert.assertEquals;

@ExtendWith(MockitoExtension.class)
class SmsServiceTests {
    @InjectMocks
    private SmsService smsService;

    @Mock
    private UserService userService;
    @Mock
    private QuestionService questionService;
    @Mock
    private MessageUtils messageUtils;
    @Mock
    private QuestionUtils questionUtils;

    private final String ADDRESS = "tel:776213875";
    private final String MESSAGE = "TEST MSG";

    @Test
    void ReturnSmsReceiverResponseWhenReceivedNonExistentAddress() {
        SMSReceiverResponse smsReceiverResponse = smsService.registerUser(ADDRESS);
        assertNotNull(smsReceiverResponse);
        assertEquals(smsReceiverResponse.getStatusCode(), SmsReceiverResponseCode.S0000);
    }

    @Test
    void WhenAskForQuestionShouldReturnQuestionSendSuccessfullyStatusCode() {
        given(messageUtils.retrieveQuestionNo(MESSAGE)).willReturn(2);
        given(questionService.checkIfQuestionExists(2, ADDRESS)).willReturn(new Question());
        given(userService.checkIfUserExists(ADDRESS)).willReturn(new User());
        given(userService.checkIfQuestionAlreadyRequestedByUser(2, new User().getQuestionResults())).willReturn(true);
        given(questionUtils.getQuestionString(new Question())).willReturn("This Question");
        SMSReceiverResponse smsReceiverResponse = smsService.sendQuestion(MESSAGE, ADDRESS);
        assertNotNull(smsReceiverResponse);
        assertEquals(smsReceiverResponse.getStatusCode(), SmsReceiverResponseCode.S0001);
    }

    @Test
    void WhenAskForQuestionByNonExistentUserShouldThrowUserNotFoundException() {
        given(messageUtils.retrieveQuestionNo(MESSAGE)).willReturn(7);
        given(questionService.checkIfQuestionExists(7, ADDRESS)).willReturn(new Question());
        given(userService.checkIfUserExists(ADDRESS)).willThrow(new UserNotFoundException());
        assertThrows(
                UserNotFoundException.class, () -> smsService.sendQuestion(MESSAGE, ADDRESS)
        );
    }

    @Test
    void WhenAskedForNonExistentQuestionShouldThrowQuestionNotFoundException() {
        given(messageUtils.retrieveQuestionNo(MESSAGE)).willReturn(7);
        given(questionService.checkIfQuestionExists(7, ADDRESS)).willThrow(new QuestionNotFoundException());
        assertThrows(
                QuestionNotFoundException.class, () -> smsService.sendQuestion(MESSAGE, ADDRESS)
        );
    }
}
