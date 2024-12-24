package com.example.demo.User;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class User {
  private Integer idUser;

  @NotNull
  @Size(min = 4, max = 30)
  private String namaUser;

  @NotNull
  @Size(min = 4, max = 50)
  private String email;

  @NotNull
  @Size(min = 4, max = 60)
  private String password;

  @NotNull
  private String confirmpassword;

  private String roleUser;
}
