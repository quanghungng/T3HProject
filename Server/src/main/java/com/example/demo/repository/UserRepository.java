package com.example.demo.repository;

import com.example.demo.model.Users;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface UserRepository extends MongoRepository<Users, String> {
    List<Users> findByUsernameAndPassword(String username, String password);
}
