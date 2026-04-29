package com.dangdoan.todoapp.controller;

import com.dangdoan.todoapp.api.AuthApi;
import com.dangdoan.todoapp.model.AuthRequest;
import com.dangdoan.todoapp.model.AuthResponse;
import com.dangdoan.todoapp.service.AuthService;
import com.dangdoan.todoapp.service.AuthToken;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController implements AuthApi {

  private final AuthService authService;

  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @Override
  public ResponseEntity<AuthResponse> signup(AuthRequest authRequest) {
    var token = authService.signup(authRequest.getUsername(), authRequest.getPassword());
    return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(token));
  }

  @Override
  public ResponseEntity<AuthResponse> login(AuthRequest authRequest) {
    var token = authService.login(authRequest.getUsername(), authRequest.getPassword());
    return ResponseEntity.ok(toResponse(token));
  }

  private AuthResponse toResponse(AuthToken token) {
    return new AuthResponse()
        .accessToken(token.accessToken())
        .tokenType(AuthResponse.TokenTypeEnum.BEARER)
        .expiresIn((int) token.expiresIn());
  }
}
