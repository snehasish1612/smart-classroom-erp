package com.smartclassroom.erp.service;

import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    // Logic to save a new user to the database
    public User saveUser(User user) {
        return userRepository.save(user);
    }

    // Logic to fetch all users from the database
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}