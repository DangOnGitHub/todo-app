package com.dangdoan.todoapp.exception;

public class UsernameAlreadyTakenException extends RuntimeException {
  private final String username;

  public UsernameAlreadyTakenException(String username) {
    super("Username '" + username + "' is already taken.");
    this.username = username;
  }

  public String getUsername() {
    return username;
  }
}
