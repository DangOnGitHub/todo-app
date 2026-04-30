package com.dangdoan.todoapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final String mailFrom;
  private final String frontendUrl;

  public EmailService(
      JavaMailSender mailSender,
      @Value("${app.mail.from:noreply@todo.dangdoan.com}") String mailFrom,
      @Value("${app.mail.frontend-url:http://localhost:5173}") String frontendUrl) {
    this.mailSender = mailSender;
    this.mailFrom = mailFrom;
    this.frontendUrl = frontendUrl;
  }

  public void sendVerificationEmail(String email, String token) {
    var verifyUrl = frontendUrl + "/verify?token=" + token;

    var message = new SimpleMailMessage();
    message.setFrom(mailFrom);
    message.setTo(email);
    message.setSubject("Verify your email");
    message.setText(
        "Click the link below to verify your email:\n\n"
            + verifyUrl
            + "\n\nThis link expires in 24 hours.");

    mailSender.send(message);
  }
}
