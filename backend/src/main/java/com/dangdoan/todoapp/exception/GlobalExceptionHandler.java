package com.dangdoan.todoapp.exception;

import com.dangdoan.todoapp.model.ValidationError;
import com.dangdoan.todoapp.model.ValidationProblem;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

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

  @ExceptionHandler(EmailNotVerifiedException.class)
  ResponseEntity<ProblemDetail> handleEmailNotVerified(EmailNotVerifiedException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.UNAUTHORIZED);
    problemDetail.setDetail(ex.getMessage());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(TodoNotFoundException.class)
  ResponseEntity<ProblemDetail> handleTodoNotFound(TodoNotFoundException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problemDetail.setDetail(ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(TodoAccessDeniedException.class)
  ResponseEntity<ProblemDetail> handleTodoAccessDenied(TodoAccessDeniedException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.FORBIDDEN);
    problemDetail.setDetail(ex.getMessage());
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(EmptyUpdateException.class)
  ResponseEntity<ValidationProblem> handleEmptyUpdate(EmptyUpdateException ex) {
    var errors = List.of(new ValidationError(ex.getMessage(), "#/"));
    var problem = new ValidationProblem();
    problem.setType("https://api.todo.dangdoan.com/validation-error");
    problem.setTitle("Your request is not valid.");
    problem.setStatus(HttpStatus.UNPROCESSABLE_CONTENT.value());
    problem.setErrors(errors);
    return ResponseEntity.status(HttpStatus.UNPROCESSABLE_CONTENT)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problem);
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

  @ExceptionHandler(NoResourceFoundException.class)
  ResponseEntity<ProblemDetail> handleNoResourceFound(NoResourceFoundException ex) {
    var problemDetail = ProblemDetail.forStatus(HttpStatus.NOT_FOUND);
    problemDetail.setDetail("No endpoint at " + ex.getResourcePath() + ".");
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }

  @ExceptionHandler(RuntimeException.class)
  ResponseEntity<ProblemDetail> handleRuntimeException(RuntimeException ex) {
    log.error("Unhandled exception", ex);
    var problemDetail = ProblemDetail.forStatus(HttpStatus.INTERNAL_SERVER_ERROR);
    problemDetail.setDetail("An unexpected error occurred. Please try again later.");
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(problemDetail);
  }
}
