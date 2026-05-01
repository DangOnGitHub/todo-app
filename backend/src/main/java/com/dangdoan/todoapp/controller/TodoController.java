package com.dangdoan.todoapp.controller;

import com.dangdoan.todoapp.api.TodosApi;
import com.dangdoan.todoapp.domain.Todo;
import com.dangdoan.todoapp.model.CreateTodoRequest;
import com.dangdoan.todoapp.model.TodoResponse;
import com.dangdoan.todoapp.model.UpdateTodoRequest;
import com.dangdoan.todoapp.service.TodoService;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TodoController implements TodosApi {

  private final TodoService todoService;

  public TodoController(TodoService todoService) {
    this.todoService = todoService;
  }

  @Override
  public ResponseEntity<List<TodoResponse>> listTodos(@Nullable Boolean completed) {
    return ResponseEntity.ok(
        todoService.list(currentEmail(), completed).stream().map(this::toResponse).toList());
  }

  @Override
  public ResponseEntity<TodoResponse> createTodo(CreateTodoRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(toResponse(todoService.create(currentEmail(), request.getTitle())));
  }

  @Override
  public ResponseEntity<TodoResponse> getTodo(Long id) {
    return ResponseEntity.ok(toResponse(todoService.get(currentEmail(), id)));
  }

  @Override
  public ResponseEntity<TodoResponse> updateTodo(Long id, UpdateTodoRequest request) {
    return ResponseEntity.ok(
        toResponse(
            todoService.update(currentEmail(), id, request.getTitle(), request.getCompleted())));
  }

  @Override
  public ResponseEntity<Void> deleteTodo(Long id) {
    todoService.delete(currentEmail(), id);
    return ResponseEntity.noContent().build();
  }

  private String currentEmail() {
    return SecurityContextHolder.getContext().getAuthentication().getName();
  }

  private TodoResponse toResponse(Todo todo) {
    return new TodoResponse()
        .id(todo.id())
        .title(todo.title())
        .completed(todo.completed())
        .createdAt(OffsetDateTime.ofInstant(todo.createdAt(), ZoneOffset.UTC))
        .updatedAt(OffsetDateTime.ofInstant(todo.updatedAt(), ZoneOffset.UTC));
  }
}
