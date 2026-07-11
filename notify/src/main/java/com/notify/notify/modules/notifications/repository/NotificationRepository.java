package com.notify.notify.modules.notifications.repository;

import com.notify.notify.modules.notifications.entities.NotificationEntity;
import com.notify.notify.utils.base.repository.BaseRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class NotificationRepository extends BaseRepository<NotificationEntity, Long> {

    public NotificationRepository(JdbcTemplate jdbcTemplate) {
        super(jdbcTemplate);
    }

    @Override
    protected Class<NotificationEntity> entityType() {
        return NotificationEntity.class;
    }

    @Override
    protected String tableName() {
        return "notifications";
    }

    @Override
    public NotificationEntity insert(NotificationEntity entity) {
        String sql = "INSERT INTO notifications (recipient, channel, subject, body, status, template_name, retry_count, provider_message_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        jdbcTemplate.update(sql,
                entity.getRecipient(),
                entity.getChannel() != null ? entity.getChannel().name() : null,
                entity.getSubject(),
                entity.getBody(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getTemplateName(),
                entity.getRetryCount(),
                entity.getProviderMessageId()
        );
        return entity;
    }

    @Override
    public NotificationEntity update(NotificationEntity entity) {
        String sql = "UPDATE notifications SET recipient = ?, channel = ?, subject = ?, body = ?, status = ?, " +
                "template_name = ?, retry_count = ?, provider_message_id = ? WHERE id = ?";

        jdbcTemplate.update(sql,
                entity.getRecipient(),
                entity.getChannel() != null ? entity.getChannel().name() : null,
                entity.getSubject(),
                entity.getBody(),
                entity.getStatus() != null ? entity.getStatus().name() : null,
                entity.getTemplateName(),
                entity.getRetryCount(),
                entity.getProviderMessageId(),
                entity.getId()
        );
        return entity;
    }
}