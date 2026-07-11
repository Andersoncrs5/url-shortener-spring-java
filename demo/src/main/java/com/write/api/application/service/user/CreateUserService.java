package com.write.api.application.service.user;

import com.write.api.application.dto.notification.WelcomeEmailEventDTO;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.application.shared.annotations.UseService;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.OutboxEventModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.notification.WelcomeMessageNotificationUseCase;
import com.write.api.ports.in.user.CreateUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
@UseService
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateUserService implements CreateUserUseCase {

    IUserRepository repository;
    PasswordEncoder passwordEncoder;
    WelcomeMessageNotificationUseCase welcomeMessage;

    @Override
    @ResultTransaction
    @TrackExecutionTime("user.create")
    public Result<UserModel> create(UserModel user) {

        log.info("Starting user creation. email={}", user.getEmail());

        try {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

            log.debug("Password encoded successfully. email={}", user.getEmail());

            UserModel created = repository.insert(user);

            log.info(
                    "User created successfully. userId={}, email={}",
                    created.getId(),
                    created.getEmail()
            );

            Result<OutboxEventModel> result = welcomeMessage.execute(
                    WelcomeEmailEventDTO.create(
                            created.getId(),
                            created.getEmail(),
                            created.getName()
                    )
            );

            if (result.isFailure()) {

                log.error(
                        "Failed to create welcome notification event. userId={}, errors={}",
                        created.getId(),
                        result.getErrors()
                );

                return Result.failure(
                        result.getErrors(),
                        result.getStatusCode()
                );
            }

            log.info(
                    "Welcome notification event created successfully. userId={}, outboxId={}",
                    created.getId(),
                    result.getValue().getId()
            );

            return Result.success(created, 201);

        } catch (DataIntegrityViolationException e) {

            String message = e.getMostSpecificCause().getMessage();

            log.warn(
                    "User creation failed due to data integrity violation. email={}, reason={}",
                    user.getEmail(),
                    message
            );

            if (message != null && message.contains("uk_users_email")) {

                log.warn(
                        "User creation rejected. Email already exists. email={}",
                        user.getEmail()
                );

                return Result.failure(
                        "Email already exists",
                        409
                );
            }

            if (message != null && message.contains("uk_users_name")) {

                log.warn(
                        "User creation rejected. Username already exists. username={}",
                        user.getName()
                );

                return Result.failure(
                        "Username already exists",
                        409
                );
            }

            return Result.failure(
                    "Database integrity error: " + message,
                    400
            );

        } catch (Exception e) {

            log.error(
                    "Unexpected error while creating user. email={}",
                    user.getEmail(),
                    e
            );

            throw new InternalServerErrorException(
                    e.getMessage()
            );
        }
    }
}