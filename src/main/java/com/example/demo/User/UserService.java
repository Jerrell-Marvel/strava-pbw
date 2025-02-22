package com.example.demo.User;

import java.util.List;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class UserService {
  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PasswordEncoder passwordEncoder;

  public boolean register(User user) {
    try {
      user.setPassword(passwordEncoder.encode(user.getPassword()));
      userRepository.save(user);
      return true;
    } catch (Exception e) {
      return false;
    }
  }

  public User login(String email, String password) {
    Optional<User> userQueryRes = userRepository.findByEmail(email);
    if (!userQueryRes.isEmpty()) {
      User user = userQueryRes.get();
      if (passwordEncoder.matches(password, user.getPassword())) {
        return user;
      }
      return null;
    }
    return null;
  }

  public List<User> getMembers(int page) {
    return userRepository.findMembers(page);
  }

  public int getMemberCount() {
    return userRepository.getMemberCount();
  }

  public User getUserById(Integer idUser) {
    return userRepository.findById(idUser).orElse(null);
  }

  public void updateMember(User user) throws Exception {
    userRepository.update(user);
  }

}
