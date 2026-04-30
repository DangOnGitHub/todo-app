package com.dangdoan.todoapp.service;

import com.dangdoan.todoapp.domain.User;
import com.dangdoan.todoapp.domain.VerificationToken;
import com.dangdoan.todoapp.exception.EmailNotVerifiedException;
import com.dangdoan.todoapp.exception.InvalidCredentialsException;
import com.dangdoan.todoapp.exception.UsernameAlreadyTakenException;
import com.dangdoan.todoapp.repository.UserRepository;
import com.dangdoan.todoapp.repository.VerificationTokenRepository;
import com.dangdoan.todoapp.security.JwtService;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService implements UserDetailsService {

  private static final long VERIFICATION_TOKEN_EXPIRY_SECONDS = 24 * 60 * 60;

  private final UserRepository userRepository;
  private final VerificationTokenRepository tokenRepository;
  private final PasswordEncoder passwordEncoder;
  private final JwtService jwtService;
  private final EmailService emailService;

  public AuthService(
      UserRepository userRepository,
      VerificationTokenRepository tokenRepository,
      PasswordEncoder passwordEncoder,
      JwtService jwtService,
      EmailService emailService) {
    this.userRepository = userRepository;
    this.tokenRepository = tokenRepository;
    this.passwordEncoder = passwordEncoder;
    this.jwtService = jwtService;
    this.emailService = emailService;
  }

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    return userRepository
        .findByEmail(username)
        .filter(User::emailVerified)
        .map(
            user ->
                new org.springframework.security.core.userdetails.User(
                    user.email(),
                    user.passwordHash(),
                    List.of(new SimpleGrantedAuthority("ROLE_USER"))))
        .orElseThrow(() -> new UsernameNotFoundException(username));
  }

  public void signup(String email, String password) {
    if (userRepository.findByEmail(email).isPresent()) {
      throw new UsernameAlreadyTakenException(email);
    }
    var user = userRepository.save(new User(null, email, passwordEncoder.encode(password), false));

    var token = UUID.randomUUID().toString();
    var expiresAt = Instant.now().plusSeconds(VERIFICATION_TOKEN_EXPIRY_SECONDS);
    tokenRepository.save(new VerificationToken(null, user.id(), token, expiresAt, Instant.now()));

    emailService.sendVerificationEmail(email, token);
  }

  public AuthToken login(String email, String password) {
    var user = userRepository.findByEmail(email).orElseThrow(InvalidCredentialsException::new);
    if (!passwordEncoder.matches(password, user.passwordHash())) {
      throw new InvalidCredentialsException();
    }
    if (!user.emailVerified()) {
      throw new EmailNotVerifiedException();
    }
    return buildToken(email);
  }

  public AuthToken verifyEmail(String token) {
    var verificationToken =
        tokenRepository.findByToken(token).orElseThrow(InvalidCredentialsException::new);

    if (Instant.now().isAfter(verificationToken.expiresAt())) {
      throw new InvalidCredentialsException();
    }

    var user = userRepository.findById(verificationToken.userId()).orElseThrow();
    var verifiedUser = new User(user.id(), user.email(), user.passwordHash(), true);
    userRepository.save(verifiedUser);

    tokenRepository.deleteByUserId(user.id());

    return buildToken(user.email());
  }

  private AuthToken buildToken(String email) {
    return new AuthToken(jwtService.generate(email), jwtService.getExpirationSeconds());
  }
}
