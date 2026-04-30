package com.dangdoan.todoapp.exception;

public class EmailNotVerifiedException extends RuntimeException {
  public EmailNotVerifiedException() {
    super("Email not verified. Check your inbox for verification link.");
  }
}
