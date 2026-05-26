package com.write.api.application.service.url;

import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.mapper.url.CreateUrlMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.enums.UrlAccessTypeEnum;
import com.write.api.core.domain.enums.UrlStatusEnum;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.ports.in.url.CreateUrlUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import com.write.api.shared.tx.ResultTransaction;
import com.write.api.shared.utils.Base62;
import lombok.RequiredArgsConstructor;
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
public class CreateUrlService implements CreateUrlUseCase {

    private final CreateUrlMapper mapper;
    private final IUrlRepository repository;
    private final PasswordEncoder passwordEncoder;
    private final SnowflakeIdGenerator idGen;

    @Override
    @ResultTransaction
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
