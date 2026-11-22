package com.example.finpilot.service;

import com.example.finpilot.entity.User;
import com.example.finpilot.exception.EmailAlreadyExistsException;
import com.example.finpilot.exception.UserNotFoundException;
import com.example.finpilot.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public Page<User> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional
    public User registerUser(String name, String email) {

        if (userRepository.existsByEmail(email)) {
            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        User user = new User(name, email);

        return userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public User getUserByEmail(String email) {

        return userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );
    }

    @Transactional(readOnly = true)
    public User getUserById(Long id) {

        return userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );
    }

    @Transactional
    public User updateUser(Long id, String name, String email) {

        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new UserNotFoundException("User not found")
                );

        if (!user.getEmail().equals(email)
                && userRepository.existsByEmail(email)) {

            throw new EmailAlreadyExistsException(
                    "Email is already registered"
            );
        }

        user.setName(name);
        user.setEmail(email);
      // because of dirty checking we dont need to write userRespository.save(user);
        return user;
    }
}