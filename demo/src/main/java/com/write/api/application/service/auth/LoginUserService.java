package com.write.api.application.service.auth;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.shared.Result;
import com.write.api.config.security.jwt.TokenService;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.auth.LoginUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LoginUserService implements LoginUserUseCase {

    private final TokenService service;
    private final PasswordEncoder passwordEncoder;
    private final IUserRepository repository;

    @Override
    public Result<AuthTokenResponseDTO> login(LoginUserDTO dto) {
        UserModel user = repository.findByEmailIgnoreCase(dto.email()).orElse(null);

        if (user == null) {
            return Result.failure(401, "Access denied");
        }

        if (user.getBlockedAt() != null &&
                user.getBlockedAt().isAfter(LocalDateTime.now())) {

            return Result.failure(423, "User blocked");
        }

        boolean matches = this.passwordEncoder.matches(
                dto.password(),
                user.getPasswordHash()
        );

        if (!matches) {
            user.sumAttemptLogin();

            if (user.getAttemptsLoginFailed() >= 3) {
                user.setBlockedAt(LocalDateTime.now().plusHours(4));
            }

            repository.save(user);

            if (user.getAttemptsLoginFailed() >= 3) {
                return Result.failure(423, "User blocked");
            }

            return Result.failure(401, "Access denied");
        }

        user.setAttemptsLoginFailed(0);
        user.setBlockedAt(null);
        user.setLastLoginAt(LocalDateTime.now());

        AuthTokenResponseDTO tokens = this.service.createTokens(user);

        user.setRefreshToken(tokens.refreshToken());
        repository.save(user);

        return Result.success(tokens);
    }


}
