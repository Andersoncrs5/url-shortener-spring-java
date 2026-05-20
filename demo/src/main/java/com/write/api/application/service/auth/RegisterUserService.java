package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.mapper.auth.RegisterUserMapper;
import com.write.api.application.shared.Result;
import com.write.api.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.auth.RegisterUserUseCase;
import com.write.api.ports.in.user.CreateUserUseCase;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterUserService implements RegisterUserUseCase {

    private final CreateUserUseCase createUser;
    private final RegisterUserMapper registerUserMapper;
    private final TokenService service;

    @ResultTransaction
    public Result<AuthTokenResponseDTO> execute(CreateUserDTO dto) {
        Result<UserModel> result = createUser.create(registerUserMapper.toDomain(dto));
        UserModel user;

        if (result.isFailure()) return Result.failure(result.getErrors(), result.getStatusCode());

        user = result.getValue();

        AuthTokenResponseDTO tokens = this.service.createTokens(user);

        return Result.success(tokens);
    }

}
