package com.ideamart.test.controller;

import com.ideamart.test.constants.Message;
import com.ideamart.test.dto.SMSReceiverRequest;
import com.ideamart.test.exception.UserAlreadyExistsException;
import com.ideamart.test.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Slf4j
@RequestMapping("/api/user")
@Controller
public class UserController {
    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> receiveUserMessageAndSendMessage(@Validated @RequestBody SMSReceiverRequest smsReceiver) {
        String senderAddress = smsReceiver.getSourceAddress();

        if (senderAddress == null) {
            return new ResponseEntity<>(Message.ADDRESSEMPTY.toString(), HttpStatus.BAD_REQUEST);
        }

        try {
            userService.saveUser(senderAddress);
            return new ResponseEntity<>(Message.SUCCESS.toString(), HttpStatus.OK);
        } catch (UserAlreadyExistsException e) {
            return new ResponseEntity<>(Message.USERALREADYEXISTS.toString(), HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            log.error(e.getMessage());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
