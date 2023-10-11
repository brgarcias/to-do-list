package com.brgarcias.todolist.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    @PostMapping("")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        UserModel existingUser = this.userRepository.findByUsername(userModel.getUsername());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists.");
        }

        var hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(hashedPassword);
        UserModel userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    @GetMapping("")
    public List<UserModel> getUsers() {
        List<UserModel> users = this.userRepository.findAll();
        return users;
    }

    @GetMapping(":id")
    public ResponseEntity getUserById(@RequestParam UUID id) {
        Optional<UserModel> user = this.userRepository.findById(id);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User not exists.");
        }
        return ResponseEntity.status(HttpStatus.FOUND).body(user);
    }
}
