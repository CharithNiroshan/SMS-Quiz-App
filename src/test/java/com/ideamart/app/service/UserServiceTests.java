package com.ideamart.app.service;

import com.ideamart.app.model.User;
import com.ideamart.app.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.BDDMockito.given;
import static org.testng.Assert.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {
    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    private List<User> users;

    @BeforeEach
    public void init() {
        users = new ArrayList<>();
        users.add(new User("tel:776213875"));
        users.add(new User("tel:711915875"));
        users.add(new User("tel:711797719"));
    }

    @Test
    void shouldReturnTrueWhenReceivedSameAnswers() {
        assertTrue(userService.checkAnswer(2, 2));
    }

    @Test
    void shouldReturnFalseWhenReceivedDifferentAnswers() {
        assertFalse(userService.checkAnswer(4, 2));
    }

    @Test
    void shouldReturnFindAll() {
        given(userRepository.findAll()).willReturn(users);
        List<User> expectedUsers = userService.getAllUsers();
        assertEquals(expectedUsers, users);
        assertEquals(expectedUsers.size(), 3);
    }
}
