package com.dangdoan.todoapp.domain;

import java.time.Instant;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("todos")
public record Todo(
    @Id Long id,
    @Column("user_id") Long userId,
    String title,
    boolean completed,
    @Column("created_at") Instant createdAt,
    @Column("updated_at") Instant updatedAt) {}
