package com.dangdoan.todoapp.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("verification_tokens")
public record VerificationToken(
    @Id Long id,
    @Column("user_id") Long userId,
    String token,
    @Column("expires_at") Instant expiresAt,
    @Column("created_at") Instant createdAt) {}
