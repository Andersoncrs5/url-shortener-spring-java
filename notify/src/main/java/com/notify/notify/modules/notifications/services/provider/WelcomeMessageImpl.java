package com.notify.notify.modules.notifications.services.provider;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.notify.notify.globals.classes.outbox.OutboxCdcEvent;
import com.notify.notify.globals.classes.result.Result;
import com.notify.notify.globals.enums.HTMLfile;
import com.notify.notify.globals.enums.NotificationChannel;
import com.notify.notify.globals.enums.NotificationStatus;
import com.notify.notify.modules.email.dto.WelcomeEmailEventDTO;
import com.notify.notify.modules.email.services.base.EmailService;
import com.notify.notify.modules.notifications.entities.NotificationEntity;
import com.notify.notify.modules.notifications.services.base.WelcomeMessage;
import com.notify.notify.utils.annotations.UseService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class WelcomeMessageImpl implements WelcomeMessage {

    EmailService emailService;
    ObjectMapper mapper;

    @Override
    public Result<Void> execute(OutboxCdcEvent outboxEvent) {
        if (!"WELCOME_EMAIL".equals(outboxEvent.eventType())) {
            log.debug("Skipping event type: {}", outboxEvent.eventType());
            return Result.failure("Type event error", HttpStatus.BAD_REQUEST);
        }

        NotificationEntity noti = new NotificationEntity();
        noti.setRecipient(null);
        noti.setChannel(NotificationChannel.EMAIL);
        noti.setSubject("Welcome to url shorter");
        noti.setBody(outboxEvent.payload());
        noti.setTemplateName("WELCOME_MESSAGE");
        noti.setStatus(NotificationStatus.PENDING);
        noti.setRetryCount(0);

        try {
            WelcomeEmailEventDTO dto = mapper.readValue(noti.getBody(), new TypeReference<>() {});

            Result<Void> result = emailService.sendWelcome(
                    dto.email(),
                    noti.getSubject(),
                    dto
            );

            if (result.isFailure()) {
                return Result.failure(result.getErrors(), result.getStatusCode());
            }

            log.info("Welcome email successfully processed for user={}", dto.userId());
            return Result.success();

        } catch (Exception e) {
            log.error("Error parsing or sending welcome email from outbox payload", e);
            throw new RuntimeException(e);
        }
    }
}