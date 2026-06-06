package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.outbox.CreateOutboxEventCommand;
import com.write.api.application.dto.outbox.events.user.UserCreatedEvent;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.mapper.auth.RegisterUserMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.enums.AggregateTypeEnum;
import com.write.api.core.domain.enums.EventTypeEnum;
import com.write.api.core.domain.enums.TopicEnum;
import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.auth.RegisterUserUseCase;
import com.write.api.ports.in.outbox.CreateOutboxEventUseCase;
import com.write.api.ports.in.user.CreateUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RegisterUserService implements RegisterUserUseCase {

    CreateUserUseCase createUser;
    RegisterUserMapper registerUserMapper;
    IUserRepository repository;
    TokenService service;
    CreateOutboxEventUseCase outbox;

    @ResultTransaction
    @TrackExecutionTime("auth.register")
    public Result<AuthTokenResponseDTO> execute(CreateUserDTO dto) {
        Result<UserModel> result = createUser.create(registerUserMapper.toDomain(dto));
        UserModel user;

        if (result.isFailure()) return Result.failure(result.getErrors(), result.getStatusCode());

        user = result.getValue();

        AuthTokenResponseDTO tokens = this.service.createTokens(user);

        user.setRefreshToken(tokens.refreshToken());
        UserModel saved = repository.save(user);

        var outboxResult = outbox.execute(
            new CreateOutboxEventCommand(
                AggregateTypeEnum.USER,
                saved.getId(),
                EventTypeEnum.USER_CREATED,
                TopicEnum.USER_CREATED,
                UserCreatedEvent.create(
                        user.getId(),
                        user.getName(),
                        user.getEmail()
                )
            )
        );

        if (outboxResult.isFailure()) return Result.failure(outboxResult.getErrors(), outboxResult.getStatusCode());

        return Result.success(tokens);
    }

}
