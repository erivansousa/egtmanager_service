package com.erivan.gtmanager.data;

import com.erivan.gtmanager.data.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
