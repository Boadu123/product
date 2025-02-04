package com.example.product.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.product.Model.UserModel;
import com.example.product.Repository.UserRepository;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<UserModel> getAllUsers(){
        return userRepository.findAll();
    }

    public void registerUser(UserModel user){

        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already in use.");
        }
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }

    public boolean authenticate(String email, String password) {

        Optional<UserModel> userOptional = userRepository.findByEmail(email);
        
        if (userOptional.isPresent()) {
            UserModel user = userOptional.get();
            
            return passwordEncoder.matches(password, user.getPassword());
        }
        
        return false;
    }

    public UserModel getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }

    public void deleteUserByEmail(String email) {
        UserModel user = userRepository.findByEmail(email).orElse(null);
        
        if (user != null) {
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }

    public void saveUser(UserModel user) {
        userRepository.save(user);
    }

}
