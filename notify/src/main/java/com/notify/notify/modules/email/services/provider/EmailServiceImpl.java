package com.notify.notify.modules.email.services.provider;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.email.dto.WelcomeEmailEventDTO;
import com.notify.notify.modules.email.services.base.EmailService;
import com.notify.notify.utils.annotations.UseService;
import jakarta.mail.internet.MimeMessage;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class EmailServiceImpl implements EmailService {

    JavaMailSender mailSender;

    @Override
    public void sendSimple(String to, String subject, String body) {

    }

    @Override
    public Result<Void> sendHtml(String to, String subject, String html) {
        return Result.success();
    }

    @Override
    public Result<Void> sendHtml(String to, String subject, String html, String templateName) {
        return null;
    }

    @Override
    public Result<Void> sendWelcome(String to, String subject, WelcomeEmailEventDTO dto) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/welcome-email.html");
            String htmlTemplate = Files.readString(resource.getFile().toPath(), StandardCharsets.UTF_8);

            String finalHtml = htmlTemplate
                    .replace("{{name}}", dto.name())
                    .replace("{{userId}}", String.valueOf(dto.userId()))
                    .replace("{{email}}", dto.email());

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("no-reply@urlshorter.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(finalHtml, true);

            mailSender.send(message);

            log.info("Welcome template email successfully sent to {}", to);
            return Result.success();

        } catch (Exception e) {
            log.error("Failed to process and send HTML template email to {}", to, e);
            return Result.failure("Email template processing failed: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void sendWithTemplate(String to, String templateName, Object variables) {

    }

    @Override
    public boolean isHealthy() {
        return true;
    }
}
