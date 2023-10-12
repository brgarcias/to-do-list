package com.brgarcias.todolist.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import at.favre.lib.crypto.bcrypt.BCrypt;

@RestController
@RequestMapping("/users")
public class UserController {

    @Autowired
    private IUserRepository userRepository;

    /**
     * Creates a new user.
     *
     * @param userModel The details of the user to be created.
     * @return ResponseEntity with created user and corresponding HTTP status code
     */
    @PostMapping
    public ResponseEntity<?> create(@RequestBody UserModel userModel) {
        UserModel existingUser = this.userRepository.findByUsername(userModel.getUsername());
        if (existingUser != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("User already exists.");
        }

        var hashedPassword = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(hashedPassword);
        UserModel userCreated = this.userRepository.save(userModel);
        return ResponseEntity.status(HttpStatus.CREATED).body(userCreated);
    }

    /**
     * Gets the list of all users.
     *
     * @return ResponseEntity with the user list and corresponding HTTP status code
     */
    @GetMapping
    public ResponseEntity<List<UserModel>> getUsers() {
        List<UserModel> tasks = this.userRepository.findAll();
        return new ResponseEntity<>(tasks, HttpStatus.OK);
    }

    /**
     * Gets user by id.
     *
     * @return ResponseEntity with the user and corresponding HTTP status code
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable UUID id) {
        Optional<UserModel> user = this.userRepository.findById(id);
        if (user.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found.");
        }
        return ResponseEntity.status(HttpStatus.OK).body(user.get());
    }
}
