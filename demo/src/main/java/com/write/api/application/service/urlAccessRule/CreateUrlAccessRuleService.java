package com.write.api.application.service.urlAccessRule;

import com.google.common.net.InetAddresses;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.urlAccessRule.UrlAccessRuleCreatedEvent;
import com.write.api.application.dto.urlAccessRule.CreateUrlAccessRuleDTO;
import com.write.api.application.mapper.urlAccessRule.CreateUrlAccessRuleMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.core.domain.enums.UrlAccessRuleTypeEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.urlAccessRule.CreateUrlAccessRuleUseCase;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import com.write.api.ports.out.repository.IUrlRepository;
import com.write.api.shared.db.DatabaseConstraintHandler;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;

@Slf4j @Service @Validated @RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateUrlAccessRuleService implements CreateUrlAccessRuleUseCase {
    SnowflakeIdGenerator idGen;
    IUrlAccessRuleRepository repository;
    IUrlRepository urlRepository;
    CreateUrlAccessRuleMapper mapper;
    CreateOutboxEventUseCase outbox;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.access.create")
    public Result<UrlAccessRuleModel> execute(CreateUrlAccessRuleDTO dto, Long assignedByUserId) {
        int count = repository.countByUrlId(dto.urlId());

        if (count >= 50) {
            return Result.failure("Number max of rule is 50", 400);
        }

        if (dto.type() == UrlAccessRuleTypeEnum.MAX_CLICKS) {
            try {
                long maxClicks = Long.parseLong(dto.ruleValue());

                if (maxClicks <= 0) {
                    return Result.failure(
                            "MAX_CLICKS must be greater than zero",
                            400
                    );
                }

            } catch (NumberFormatException e) {
                return Result.failure(
                        "MAX_CLICKS must be a valid number",
                        400
                );
            }
        }

        if (dto.type() == UrlAccessRuleTypeEnum.RATE_LIMIT) {
            try {
                long rateLimit = Long.parseLong(dto.ruleValue());

                if (rateLimit <= 0) {
                    return Result.failure(
                            "RATE_LIMIT must be greater than zero",
                            400
                    );
                }

            } catch (NumberFormatException e) {
                return Result.failure(
                        "RATE_LIMIT must be a valid number",
                        400
                );
            }
        }

        if (dto.type() == UrlAccessRuleTypeEnum.EXPIRES_AT) {

            if (dto.expiresAt() == null) {
                return Result.failure(
                        "EXPIRES_AT requires an expiration date",
                        400
                );
            }

            if (dto.expiresAt().isBefore(LocalDateTime.now())) {
                return Result.failure(
                        "EXPIRES_AT must be a future date",
                        400
                );
            }
        }

        if (
                (dto.type() == UrlAccessRuleTypeEnum.COUNTRY_ALLOW ||
                        dto.type() == UrlAccessRuleTypeEnum.COUNTRY_BLOCK)
                        &&
                        !dto.ruleValue().matches("^[A-Z]{2}$")
        ) {
            return Result.failure(
                    "Country code must be a valid ISO-3166 alpha-2 code (e.g. BR, US, FR)",
                    400
            );
        }

        if (
                dto.type() == UrlAccessRuleTypeEnum.IP_ALLOW ||
                        dto.type() == UrlAccessRuleTypeEnum.IP_BLOCK
        ) {
            if (!InetAddresses.isInetAddress(dto.ruleValue())) {
                return Result.failure(
                        "Invalid IP address",
                        400
                );
            }
        }

        if (
                dto.type() == UrlAccessRuleTypeEnum.USER_AGENT_BLOCK &&
                        dto.ruleValue().isBlank()
        ) {
            return Result.failure(
                    "User agent cannot be empty",
                    400
            );
        }

        boolean isBelong = urlRepository.existsByUserIdAndUrlId(assignedByUserId, dto.urlId());

        if (!isBelong) {
            return Result.failure(
                    "You do not have permission to manage this URL",
                    403
            );
        }

        UrlAccessRuleModel access = mapper.toDomain(dto);
        access.setRuleValue(dto.ruleValue());
        access.setAssignedByUserId(assignedByUserId);
        access.setId(idGen.nextId());

        try {
            UrlAccessRuleModel inserted = repository.insert(access);

            var outboxResult = outbox.execute(
                    new CreateOutboxEventCommand(
                            AggregateTypeEnum.URL_ACCESS_RULE,
                            inserted.getId(),
                            EventTypeEnum.URL_ACCESS_RULE_CREATED,
                            TopicEnum.URL_ACCESS_RULE_CREATED,
                            UrlAccessRuleCreatedEvent.create(
                                    inserted.getId(),
                                    inserted.getUrlId(),
                                    inserted.getAssignedByUserId(),
                                    inserted.getRuleValue(),
                                    inserted.getType(),
                                    inserted.getCreatedAt()
                            )
                    )
            );

            if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

            return Result.success(inserted, 201);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            String normalized = message.toLowerCase();

            if (normalized.contains("uk_url_access_rule")) {
                return Result.failure(
                        "Access rule already present for this url",
                        409
                );
            }

            if (normalized.contains("fk_url_access_rule_url_id")) {
                return Result.failure(
                        "Url not found",
                        404
                );
            }

            if (normalized.contains("fk_url_access_rule_user_id")) {
                return Result.failure(
                        "Assigned user not found",
                        404
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {
            log.error(
                    "Error creating access rule",
                    e
            );

            throw new InternalServerErrorException(
                    e.getMessage()
            );
        }

    }
}
