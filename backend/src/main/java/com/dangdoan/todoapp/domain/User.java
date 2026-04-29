package com.dangdoan.todoapp.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("users")
public record User(@Id Long id, String username, @Column("password_hash") String passwordHash) {}
