package com.dangdoan.todoapp.service;

import com.dangdoan.todoapp.domain.User;
import com.dangdoan.todoapp.exception.InvalidCredentialsException;
import com.dangdoan.todoapp.exception.UsernameAlreadyTakenException;
import com.dangdoan.todoapp.repository.UserRepository;
import com.dangdoan.todoapp.security.JwtService;
import java.util.List;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;

  public AuthService(
      UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByUsername(username)
        .map(
            user ->
                new org.springframework.security.core.userdetails.User(
                    user.username(),
                    user.passwordHash(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))))
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  public AuthToken signup(String username, String password) {
    if (userRepository.findByUsername(username).isPresent()) {
      throw new UsernameAlreadyTakenException(username);
    }
    userRepository.save(new User(null, username, passwordEncoder.encode(password)));
    return buildToken(username);
  }

  public AuthToken login(String username, String password) {
    var user =
        userRepository.findByUsername(username).orElseThrow(InvalidCredentialsException::new);
    if (!passwordEncoder.matches(password, user.passwordHash())) {
      throw new InvalidCredentialsException();
    }
    return buildToken(username);
  }

  private AuthToken buildToken(String username) {
    return new AuthToken(jwtService.generate(username), jwtService.getExpirationSeconds());
  }
}
