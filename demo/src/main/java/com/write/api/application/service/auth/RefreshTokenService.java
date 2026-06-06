package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.infrastructure.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.auth.RefreshTokenUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RefreshTokenService implements RefreshTokenUseCase {

    private final IUserRepository repository;
    private final TokenService service;

    @Override
    @ResultTransaction
    @TrackExecutionTime("auth.refresh.token")
    public Result<AuthTokenResponseDTO> execute(String refreshToken) {
        Result<String> validated = this.service.validateTokenV2(refreshToken);
        if (validated.isFailure()) return Result.failure(validated.getStatusCode(), validated.getMessage());

        UserModel user = this.repository.findByRefreshToken(refreshToken).orElse(null);

        if (user == null) return Result.failure(404, "User not found");

        if (!validated.getValue().equals(user.getId().toString())) {
            return Result.failure(401, "Token mismatch");
        }

        AuthTokenResponseDTO tokens = this.service.createTokens(user);

        user.setRefreshToken(tokens.refreshToken());
        repository.save(user);

        return Result.success(tokens);
    }

}
