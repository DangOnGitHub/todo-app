package com.dangdoan.todoapp.repository;

import com.dangdoan.todoapp.domain.VerificationToken;
import java.util.Optional;
import org.springframework.data.repository.CrudRepository;

public interface VerificationTokenRepository extends CrudRepository<VerificationToken, Long> {
  Optional<VerificationToken> findByToken(String token);

  void deleteByUserId(Long userId);
}
