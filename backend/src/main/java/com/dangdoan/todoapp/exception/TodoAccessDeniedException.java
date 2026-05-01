package com.dangdoan.todoapp.exception;

public class TodoAccessDeniedException extends RuntimeException {
  public TodoAccessDeniedException() {
    super("You do not have permission to access this todo.");
  }
}
