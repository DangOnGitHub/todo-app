package com.dangdoan.todoapp.exception;

import com.dangdoan.todoapp.model.ValidationError;
import com.dangdoan.todoapp.model.ValidationProblem;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(UsernameAlreadyTakenException.class)
  ResponseEntity<ProblemDetail> handleUsernameAlreadyTaken(UsernameAlreadyTakenException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.CONFLICT);
    problemDetail.setDetail(ex.getMessage());
    return ResponseEntity.status(HttpStatus.CONFLICT)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  ResponseEntity<ProblemDetail> handleInvalidCredentials(InvalidCredentialsException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setDetail(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  ResponseEntity<ValidationProblem> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex) {
    var errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(error -> new ValidationError(error.getDefaultMessage(), "#/" + error.getField()))
            .toList();
    var problem = new ValidationProblem();
    problem.setType("https://api.todo.dangdoan.com/validation-error");
    problem.setTitle("Your request is not valid.");
    problem.setStatus(HttpStatus.UNPROCESSABLE_CONTENT.value());
    problem.setErrors(errors);
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  ResponseEntity<ProblemDetail> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.BAD_REQUEST);
    problemDetail.setDetail("Malformed request body.");
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(RuntimeException.class)
  ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setDetail("An unexpected error occurred. Please try again later.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }
}
