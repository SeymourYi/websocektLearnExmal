package org.example.backend.service;

import org.example.backend.entity.User;
import org.example.backend.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User createUser(String username, String password) {
    if (userRepository.existsByUsername(username)) {
      throw new RuntimeException("用户名已存在");
    }

    User user = new User();
    user.setUsername(username);
    user.setPassword(password); // 实际应用中应该对密码进行加密

    return userRepository.save(user);
  }

  public Optional<User> getUserByUsername(String username) {
    return userRepository.findByUsername(username);
  }

  public Optional<User> getUserById(Long id) {
    return userRepository.findById(id);
  }
}