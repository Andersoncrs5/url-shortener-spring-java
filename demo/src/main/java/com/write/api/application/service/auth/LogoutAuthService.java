package com.write.api.application.service.auth;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.auth.LogoutAuthUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class LogoutAuthService implements LogoutAuthUseCase {

    private final IUserRepository repository;

    @Override
    @ResultTransaction
    @TrackExecutionTime("auth.logout")
    public Result<UserModel> execute(Long id) {
        UserModel user = repository.findById(id).orElse(null);

        if (user == null) return Result.failure(404, "User not found");

        user.setRefreshToken(null);

        UserModel saved = this.repository.save(user);
        log.info("User {} make logout", user.getEmail());
        return Result.success(saved);
    }
}
