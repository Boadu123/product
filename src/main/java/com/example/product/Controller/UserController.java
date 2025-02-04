package com.example.product.Controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import com.example.product.Model.UserModel;
import com.example.product.Repository.UserRepository;

import jakarta.validation.Valid;

import com.example.product.Service.JwtService;
import com.example.product.Service.UserService;

@RestController
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @GetMapping(value = "/users")
    public ResponseEntity<Map<String, Object>> getUsers(@RequestHeader(value = "Authorization", required = false) String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            
            List<UserModel> users = userService.getAllUsers();

            if (users.isEmpty()) {
                response.put("status", "error");
                response.put("message", "No users Found");
                response.put("details", new ArrayList<>());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }
    

            response.put("status", "success");
            response.put("message", "All users are available here.");
            response.put("details", users);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while fetching users.");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PostMapping(value = "/user")
    public ResponseEntity<Map<String, Object>> register(@Valid @RequestBody UserModel user, BindingResult result) {
        Map<String, Object> response = new HashMap<>();

        if (result.hasErrors()) {
            Map<String, String> validationErrors = new HashMap<>();
            result.getFieldErrors().forEach(error -> validationErrors.put(error.getField(), error.getDefaultMessage()));
            response.put("status", "error");
            response.put("message", "Validation failed");
            response.put("details", validationErrors);
            return ResponseEntity.badRequest().body(response);
        }

        try {
            userService.registerUser(user);
            response.put("status", "success");
            response.put("message", "User added successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            response.put("status", "error");
            response.put("message", "Error occurred while adding user");
            response.put("details", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, Object>> login(@RequestBody UserModel userModel) {
        Map<String, Object> response = new HashMap<>();

        // Check if the user exists in the database
        Optional<UserModel> optionalUser = userRepository.findByEmail(userModel.getEmail());

        if (optionalUser.isPresent()) {
            UserModel user = optionalUser.get();

            // Check if the password is correct
            boolean isAuthenticated = userService.authenticate(user.getEmail(), userModel.getPassword());

            if (isAuthenticated) {
                // Generate token with the correct user ID
                String token = jwtService.generateToken(user.getEmail(), user.getId());

                response.put("status", "success");
                response.put("token", token);
                return ResponseEntity.ok(response);
            } else {
                response.put("status", "error");
                response.put("message", "Invalid credentials");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }
        } else {
            response.put("status", "error");
            response.put("message", "User not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }


    @GetMapping("/user")
    public ResponseEntity<Map<String, Object>> getLoggedInUserDetails(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("status", "error");
                response.put("message", "Authorization header is missing or invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract token from the header
            String token = authHeader.substring(7);

            // Validate and extract email (or username) from the token
            if (!jwtService.validateToken(token)) {
                response.put("status", "error");
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            String email = jwtService.extractUsername(token); // Extract email/username from the token

            // Retrieve user details from the database using the email
            UserModel user = userService.getUserByEmail(email);

            if (user == null) {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            response.put("status", "success");
            response.put("message", "User details retrieved successfully");
            response.put("details", user);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while fetching user details");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @DeleteMapping("/user")
    public ResponseEntity<Map<String, Object>> deleteUser(@RequestHeader(value = "Authorization", required = true) String authHeader) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if the Authorization header exists and starts with "Bearer"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("status", "error");
                response.put("message", "Authorization header is missing or invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract token from the Authorization header
            String token = authHeader.substring(7);

            // Validate the token
            if (!jwtService.validateToken(token)) {
                response.put("status", "error");
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract the username (email) from the token
            String email = jwtService.extractUsername(token);

            // Retrieve the user details from the database using the email
            UserModel user = userService.getUserByEmail(email);

            // Check if the user exists
            if (user == null) {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Delete the user from the database
            userService.deleteUserByEmail(email);

            response.put("status", "success");
            response.put("message", "User deleted successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while deleting user");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/user")
    public ResponseEntity<Map<String, Object>> updateUser(@RequestHeader(value = "Authorization", required = true) String authHeader, @RequestBody UserModel updatedUser) {
        Map<String, Object> response = new HashMap<>();

        try {
            // Check if the Authorization header exists and starts with "Bearer"
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                response.put("status", "error");
                response.put("message", "Authorization header is missing or invalid");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract token from the Authorization header
            String token = authHeader.substring(7);

            // Validate the token
            if (!jwtService.validateToken(token)) {
                response.put("status", "error");
                response.put("message", "Invalid or expired token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
            }

            // Extract the username (email) from the token
            String email = jwtService.extractUsername(token);

            // Retrieve the user details from the database using the email
            UserModel user = userService.getUserByEmail(email);

            // Check if the user exists
            if (user == null) {
                response.put("status", "error");
                response.put("message", "User not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
            }

            // Update the fields except the password (do not touch the password)
            if (updatedUser.getFirstName() != null) {
                user.setFirstName(updatedUser.getFirstName());
            }
            if (updatedUser.getLastName() != null) {
                user.setLastName(updatedUser.getLastName());
            }
            if (updatedUser.getEmail() != null) {
                user.setEmail(updatedUser.getEmail());
            }
            // (add more fields as necessary)

            // Save the updated user data
            userService.saveUser(user);

            // Exclude the password when returning the user details
            user.setPassword(null); // Set password to null to exclude it

            response.put("status", "success");
            response.put("message", "User details updated successfully");
            response.put("details", user);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "An error occurred while updating user details");
            response.put("details", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

}
