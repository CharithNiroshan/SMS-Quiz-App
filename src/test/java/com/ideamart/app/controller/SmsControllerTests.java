package com.ideamart.app.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ideamart.app.constant.Message;
import com.ideamart.app.constant.RequestType;
import com.ideamart.app.constant.SmsReceiverResponseCode;
import com.ideamart.app.dto.SMSReceiverRequest;
import com.ideamart.app.dto.SMSReceiverResponse;
import com.ideamart.app.exception.QuestionNotFoundException;
import com.ideamart.app.exception.UserAlreadyExistsException;
import com.ideamart.app.exception.UserNotFoundException;
import com.ideamart.app.service.SmsService;
import com.ideamart.app.util.MessageUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SmsController.class)
class SmsControllerTests {
    final String LINK = "/mo-receiver";

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    SmsService smsService;
    @MockBean
    MessageUtils messageUtils;

    private SMSReceiverResponse smsReceiverResponse;
    private SMSReceiverRequest smsReceiverRequest;

    @BeforeEach
    public void init() {
        String version = "1.0";
        String message = "TEST";
        String sourceAddress = "tel:776213875";
        String requestId = "APP_000001";
        String encoding = "0";
        String applicationId = "APP_000001";
        smsReceiverRequest = new SMSReceiverRequest(version, applicationId, sourceAddress, message, requestId, encoding);
    }

    @Test
    void whenAskForRegistrationFromNewNumberShouldReturnSuccessCode() throws Exception {
        given(messageUtils.getRequestType(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willReturn(RequestType.REGISTER);
        smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.S0000, Message.REGISTERDSUCCESSFULLY.toString());
        given(smsService.registerUser(smsReceiverRequest.getSourceAddress())).willReturn(smsReceiverResponse);
        mockMvc.perform(
                        post(LINK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(smsReceiverRequest)
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(SmsReceiverResponseCode.S0000.toString()));
    }

    @Test
    void whenInvalidRequestIsReceivedShouldReturnE0000ErrorCode() throws Exception {
        given(messageUtils.getRequestType(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willReturn(RequestType.INVALID);
        mockMvc.perform(
                        post(LINK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(smsReceiverRequest)
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(SmsReceiverResponseCode.E0000.toString()))
                .andExpect(jsonPath("$.statusDetail").value(Message.INVALIDREQUEST.toString()));
    }

    @Test
    void whenExistingUserAskedForRegistrationShouldReturnE0001ErrorCode() throws Exception {
        given(messageUtils.getRequestType(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willReturn(RequestType.REGISTER);
        given(smsService.registerUser(smsReceiverRequest.getSourceAddress())).willThrow(new UserAlreadyExistsException());
        mockMvc.perform(
                        post(LINK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(smsReceiverRequest)
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(SmsReceiverResponseCode.E0001.toString()))
                .andExpect(jsonPath("$.statusDetail").value(Message.USERALREADYEXISTS.toString()));
    }

    @Test
    void whenNonExistingUserAskForQuestionShouldReturnE0002ErrorCode() throws Exception {
        given(messageUtils.getRequestType(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willReturn(RequestType.QUESTION);
        given(smsService.sendQuestion(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willThrow(new UserNotFoundException());
        mockMvc.perform(
                        post(LINK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(smsReceiverRequest)
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(SmsReceiverResponseCode.E0002.toString()))
                .andExpect(jsonPath("$.statusDetail").value(Message.USERNOTFOUND.toString()));
    }

    @Test
    void whenAskForNonExistentQuestionNoShouldReturnE0003ErrorCode() throws Exception {
        given(messageUtils.getRequestType(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willReturn(RequestType.QUESTION);
        given(smsService.sendQuestion(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willThrow(new QuestionNotFoundException());
        mockMvc.perform(
                        post(LINK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(smsReceiverRequest)
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(SmsReceiverResponseCode.E0003.toString()))
                .andExpect(jsonPath("$.statusDetail").value(Message.QUESTIONNOTFOUND.toString()));
    }

    @Test
    void whenAskForValidQuestionShouldReturnS0001SuccessCode() throws Exception {
        given(messageUtils.getRequestType(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willReturn(RequestType.QUESTION);
        smsReceiverResponse = new SMSReceiverResponse(SmsReceiverResponseCode.S0001, Message.QUESTIONSENDSUCCESSFULLY.toString());
        given(smsService.sendQuestion(smsReceiverRequest.getMessage(), smsReceiverRequest.getSourceAddress())).willReturn(smsReceiverResponse);
        mockMvc.perform(
                        post(LINK)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(smsReceiverRequest)
                                ))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statusCode").value(SmsReceiverResponseCode.S0001.toString()))
                .andExpect(jsonPath("$.statusDetail").value(Message.QUESTIONSENDSUCCESSFULLY.toString()));
    }
}
