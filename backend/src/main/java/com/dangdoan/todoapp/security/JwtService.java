package com.dangdoan.todoapp.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.time.Instant;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class JwtService {

  @Value("${app.jwt.secret}")
  private String secret;

  @Value("${app.jwt.expiration-seconds}")
  private long expirationSeconds;

  public String generate(String username) {
    Instant now = Instant.now();
    return Jwts.builder()
        .subject(username)
        .issuedAt(Date.from(now))
        .expiration(Date.from(now.plusSeconds(expirationSeconds)))
        .signWith(key())
        .compact();
  }

  public long getExpirationSeconds() {
    return expirationSeconds;
  }

  public String extractUsername(String token) {
    return claims(token).getSubject();
  }

  public boolean isValid(String token) {
    try {
      claims(token);
      return true;
    } catch (JwtException e) {
      return false;
    }
  }

  private Claims claims(String token) {
    return Jwts.parser().verifyWith(key()).build().parseSignedClaims(token).getPayload();
  }

  private SecretKey key() {
    return Keys.hmacShaKeyFor(Decoders.BASE64.decode(secret));
  }
}
