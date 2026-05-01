package com.dangdoan.todoapp.exception;

public class EmptyUpdateException extends RuntimeException {
  public EmptyUpdateException() {
    super("At least one field must be provided.");
  }
}
