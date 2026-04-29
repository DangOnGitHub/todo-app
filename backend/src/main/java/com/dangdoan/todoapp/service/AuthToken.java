package com.dangdoan.todoapp.service;

public record AuthToken(String accessToken, long expiresIn) {}
