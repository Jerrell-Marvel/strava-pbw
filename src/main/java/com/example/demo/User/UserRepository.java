package com.example.demo.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
  void save(User user) throws Exception;

  Optional<User> findByEmail(String email);

  List<User> findMembers(int page);

  int getMemberCount();

  Optional<User> findById(Integer idUser);

  void update(User user) throws Exception;

}
