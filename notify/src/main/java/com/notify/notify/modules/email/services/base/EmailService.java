package com.notify.notify.modules.email.services.base;

import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.modules.email.dto.WelcomeEmailEventDTO;

public interface EmailService {

    void sendSimple(String to, String subject, String body);

    Result<Void> sendHtml(String to, String subject, String html);

    Result<Void> sendHtml(String to, String subject, String html, String templateName);

    Result<Void> sendWelcome(String to, String subject, WelcomeEmailEventDTO dto);

    void sendWithTemplate(String to, String templateName, Object variables);

    boolean isHealthy();
}
