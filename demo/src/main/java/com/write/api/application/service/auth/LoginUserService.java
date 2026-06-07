package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.user.UserLoginBlockedEvent;
import com.write.api.application.dto.outbox.events.user.UserLoginFailEvent;
import com.write.api.application.dto.outbox.events.user.UserLoginSuccessEvent;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.auth.LoginUserUseCase;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LoginUserService implements LoginUserUseCase {

    TokenService service;
    PasswordEncoder passwordEncoder;
    IUserRepository repository;
    CreateOutboxEventUseCase outbox;

    @Override
    @ResultTransaction
    @TrackExecutionTime("auth.login")
    public Result<AuthTokenResponseDTO> login(LoginUserDTO dto) {
        UserModel user = repository.findByEmailIgnoreCase(dto.email()).orElse(null);

        if (user == null) {
            return Result.failure(401, "Access denied");
        }

        if (user.getBlockedAt() != null && user.getBlockedAt().isAfter(LocalDateTime.now())) {
            return Result.failure(423, "User blocked");
        }

        boolean matches = passwordEncoder.matches(dto.password(), user.getPasswordHash());

        if (!matches) {

            user.sumAttemptLogin();

            if (user.getAttemptsLoginFailed() >= 3) {

                user.setBlockedAt(LocalDateTime.now().plusHours(4));

                var outboxResult = outbox.execute(
                        new CreateOutboxEventCommand(
                                AggregateTypeEnum.USER,
                                user.getId(),
                                EventTypeEnum.USER_BLOCKED,
                                TopicEnum.USER_BLOCKED,
                                UserLoginBlockedEvent.create(
                                        user.getId(),
                                        user.getName(),
                                        user.getEmail()
                                )
                        )
                );

                if (outboxResult.isFailure()) {
                    return Result.failure(
                            outboxResult.getErrors(),
                            outboxResult.getStatusCode()
                    );
                }

                repository.save(user);

                return Result.failure(423, "User blocked");
            }

            var outboxResult = outbox.execute(
                new CreateOutboxEventCommand(
                    AggregateTypeEnum.USER,
                    user.getId(),
                    EventTypeEnum.USER_LOGIN_FAILED,
                    TopicEnum.USER_LOGIN_FAILED,
                    UserLoginFailEvent.create(
                        user.getId(),
                        user.getName(),
                        user.getEmail(),
                        user.getAttemptsLoginFailed()
                    )
                )
            );

            if (outboxResult.isFailure()) {
                return Result.failure(
                        outboxResult.getErrors(),
                        outboxResult.getStatusCode()
                );
            }

            repository.save(user);

            return Result.failure(401, "Access denied");
        }

        user.setAttemptsLoginFailed(0);
        user.setBlockedAt(null);
        user.setLastLoginAt(LocalDateTime.now());

        AuthTokenResponseDTO tokens = service.createTokens(user);

        user.setRefreshToken(tokens.refreshToken());
        repository.save(user);

        var outboxResult = outbox.execute(
                new CreateOutboxEventCommand(
                        AggregateTypeEnum.USER,
                        user.getId(),
                        EventTypeEnum.USER_LOGIN_SUCCESS,
                        TopicEnum.USER_LOGIN_SUCCESS,
                        UserLoginSuccessEvent.create(
                                user.getId(),
                                user.getName(),
                                user.getEmail()
                        )
                )
        );

        if (outboxResult.isFailure()) {
            return Result.failure(
                    outboxResult.getErrors(),
                    outboxResult.getStatusCode()
            );
        }

        log.info("User {} make login", user.getEmail());
        return Result.success(tokens);
    }


}
