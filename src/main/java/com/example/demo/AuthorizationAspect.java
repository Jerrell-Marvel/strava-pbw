package com.example.demo;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpSession;

@Aspect
@Component
public class AuthorizationAspect {

  @Autowired
  private HttpSession session;

  @Before("@annotation(requiredRole)")
  public void checkAuthorization(JoinPoint joinPoint, RequiredRole requiredRole) throws Throwable {

    String email = (String) session.getAttribute("email");
    if (email == null) {
      throw new SecurityException("User is not logged in.");
    }

    String currRole = (String) session.getAttribute("role");

    for (String allowedRole : requiredRole.value()) {
      if (allowedRole.equals("*") || currRole.equals(allowedRole)) {
        return;
      }
    }

    throw new SecurityException("Access denied for user role: " + currRole);
  }
}
