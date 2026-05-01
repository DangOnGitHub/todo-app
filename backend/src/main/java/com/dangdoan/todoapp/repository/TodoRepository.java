package com.dangdoan.todoapp.repository;

import com.dangdoan.todoapp.domain.Todo;
import java.util.List;
import org.springframework.data.repository.CrudRepository;

public interface TodoRepository extends CrudRepository<Todo, Long> {
  List<Todo> findByUserId(Long userId);

  List<Todo> findByUserIdAndCompleted(Long userId, boolean completed);
}
