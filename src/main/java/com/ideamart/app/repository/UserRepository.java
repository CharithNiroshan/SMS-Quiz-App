package com.ideamart.app.repository;

import com.ideamart.app.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<User,String> {
        Optional<User> findByAddress(String address);
}
