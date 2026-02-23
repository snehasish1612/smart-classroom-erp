package com.smartclassroom.erp.controller;

import com.smartclassroom.erp.entity.User;
import com.smartclassroom.erp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/users") // This is the base URL for this controller
public class UserController {

    @Autowired
    private UserService userService;

    // POST API to add a user. URL: http://localhost:8080/api/users/add
    @PostMapping("/add")
    public User addNewUser(@RequestBody User user) {
        return userService.saveUser(user);
    }

    // GET API to fetch all users. URL: http://localhost:8080/api/users/all
    @GetMapping("/all")
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }
}