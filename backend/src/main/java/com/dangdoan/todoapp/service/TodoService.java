package com.dangdoan.todoapp.service;

import com.dangdoan.todoapp.domain.Todo;
import com.dangdoan.todoapp.exception.EmptyUpdateException;
import com.dangdoan.todoapp.exception.TodoAccessDeniedException;
import com.dangdoan.todoapp.exception.TodoNotFoundException;
import com.dangdoan.todoapp.repository.TodoRepository;
import com.dangdoan.todoapp.repository.UserRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

  private final TodoRepository todoRepository;
  private final UserRepository userRepository;

  public TodoService(TodoRepository todoRepository, UserRepository userRepository) {
    this.todoRepository = todoRepository;
    this.userRepository = userRepository;
  }

  public List<Todo> list(String email, Boolean completed) {
    var userId = resolveUserId(email);
    return completed != null
        ? todoRepository.findByUserIdAndCompleted(userId, completed)
        : todoRepository.findByUserId(userId);
  }

  public Todo create(String email, String title) {
    var userId = resolveUserId(email);
    var now = Instant.now();
    return todoRepository.save(new Todo(null, userId, title, false, now, now));
  }

  public Todo get(String email, Long id) {
    var userId = resolveUserId(email);
    var todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    if (!todo.userId().equals(userId)) throw new TodoAccessDeniedException();
    return todo;
  }

  public Todo update(String email, Long id, String title, Boolean completed) {
    if (title == null && completed == null) throw new EmptyUpdateException();
    var userId = resolveUserId(email);
    var todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    if (!todo.userId().equals(userId)) throw new TodoAccessDeniedException();

    var newTitle = title != null ? title : todo.title();
    var newCompleted = completed != null ? completed : todo.completed();
    var changed = !newTitle.equals(todo.title()) || newCompleted != todo.completed();
    var updatedAt = changed ? Instant.now() : todo.updatedAt();

    return todoRepository.save(
        new Todo(todo.id(), userId, newTitle, newCompleted, todo.createdAt(), updatedAt));
  }

  public void delete(String email, Long id) {
    var userId = resolveUserId(email);
    var todo = todoRepository.findById(id).orElseThrow(() -> new TodoNotFoundException(id));
    if (!todo.userId().equals(userId)) throw new TodoAccessDeniedException();
    todoRepository.deleteById(id);
  }

  private Long resolveUserId(String email) {
    return userRepository.findByEmail(email).orElseThrow().id();
  }
}
