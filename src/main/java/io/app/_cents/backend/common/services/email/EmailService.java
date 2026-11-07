package io.app._cents.backend.common.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

  private final JavaMailSender mailSender;
  private final String fromAddress;

  public EmailService(
      JavaMailSender mailSender, @Value("${spring.mail.username}") String fromAddress) {
    this.mailSender = mailSender;
    this.fromAddress = fromAddress;
  }

  public void send(String to, String subject, String body) {
    SimpleMailMessage message = new SimpleMailMessage();
    message.setFrom(fromAddress);
    message.setTo(to);
    message.setSubject(subject);
    message.setText(body);
    mailSender.send(message);
  }

  public void sendHtml(String to, String subject, String htmlBody) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, "UTF-8");
      helper.setFrom(fromAddress);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(htmlBody, true); // true = treat as HTML
      mailSender.send(message);
    } catch (MessagingException e) {
      throw new IllegalStateException("Failed to send email", e);
    }
  }
}
