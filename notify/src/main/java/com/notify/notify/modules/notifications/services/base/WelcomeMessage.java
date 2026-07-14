package com.notify.notify.modules.notifications.services.base;

import com.notify.notify.globals.classes.outbox.OutboxCdcEvent;
import com.notify.notify.globals.classes.result.Result;

public interface WelcomeMessage {
    Result<Void> execute(OutboxCdcEvent outboxEvent);
}
