package com.dangdoan.todoapp.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

public class RequestLoggingFilter extends OncePerRequestFilter {

  private static final Logger logger = LoggerFactory.getLogger(RequestLoggingFilter.class);

  private final String podName = readEnvironmentVariable("POD_NAME");
  private final String nodeName = readEnvironmentVariable("NODE_NAME");

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    long startTimeNanos = System.nanoTime();
    try {
      filterChain.doFilter(request, response);
    } finally {
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
      String username = authentication != null ? authentication.getName() : "anonymous";
      long durationMillis = (System.nanoTime() - startTimeNanos) / 1_000_000;
      logger.info(
          "request method={} path={} status={} durationMs={} user={} pod={} node={}",
          request.getMethod(),
          request.getRequestURI(),
          response.getStatus(),
          durationMillis,
          username,
          podName,
          nodeName);
    }
  }

  private static String readEnvironmentVariable(String variableName) {
    String value = System.getenv(variableName);
    return value != null && !value.isBlank() ? value : "unknown";
  }
}
