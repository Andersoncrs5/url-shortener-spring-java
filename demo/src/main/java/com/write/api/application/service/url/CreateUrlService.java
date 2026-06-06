package com.write.api.application.service.url;

import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.url.UrlCreatedEvent;
import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.mapper.url.CreateUrlMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.*;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.url.CreateUrlUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import com.write.api.shared.tx.ResultTransaction;
import com.write.api.shared.utils.Base62;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateUrlService implements CreateUrlUseCase {

    CreateUrlMapper mapper;
    IUrlRepository repository;
    PasswordEncoder passwordEncoder;
    SnowflakeIdGenerator idGen;
    CreateOutboxEventUseCase outbox;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.create")
    public Result<UrlModel> execute(CreateUrlDTO dto, Long userId) {
        long id = idGen.nextId();
        String shortCode = Base62.encode(id);

        UrlModel model = mapper.toModel(dto);
        model.setUserId(userId);
        model.setShortCode(shortCode);
        model.setStatus(UrlStatusEnum.ACTIVE);
        model.setCustomAlias(false);

        if (dto.password() != null) {
            model.setPasswordHash(passwordEncoder.encode(dto.password()));
            model.setAccessType(UrlAccessTypeEnum.PASSWORD_PROTECTED);
        }

        try {
            UrlModel save = repository.insert(model);

            var outboxResult = outbox.execute(
                    new CreateOutboxEventCommand(
                            AggregateTypeEnum.URL,
                            save.getId(),
                            EventTypeEnum.URL_CREATED,
                            TopicEnum.URL_CREATED,
                            UrlCreatedEvent.create(
                                    save.getId(),
                                    save.getTitle(),
                                    save.getShortCode(),
                                    save.getStatus(),
                                    save.getCreatedAt()
                            )
                    )
            );

            if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

            return Result.success(save, 201);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();
            log.info(message);

            if (message == null) {
                return Result.failure(
                        "Database integrity error",
                        400
                );
            }

            if (message.contains("uk_urls_short_code")) {
                return  Result.failure(
                        "Short code " + model.getShortCode() + " already exists",
                        409
                );
            }
            if (message.contains("fk_urls_user")) {
                return Result.failure(
                        "User not found",
                        404
                );
            }

            if (message.contains("cannot be null")) {

                String column = "unknown";

                Matcher matcher = Pattern.compile("'(.*?)'")
                        .matcher(message);

                if (matcher.find()) {
                    column = matcher.group(1);
                }

                return Result.failure(
                        "Required field '" + column + "' is missing",
                        400
                );
            }

            if (message.contains("Data too long")) {

                String column = "unknown";

                Matcher matcher = Pattern
                        .compile("'(.*?)'")
                        .matcher(message);

                if (matcher.find()) {
                    column = matcher.group(1);
                }

                return Result.failure(
                        "Field '" + column + "' exceeded the allowed size",
                        400
                );
            }

            return  Result.failure(
                    "Database integrity error: " + message,
                    400
            );
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }

}
