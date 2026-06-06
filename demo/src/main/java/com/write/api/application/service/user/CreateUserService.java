package com.write.api.application.service.user;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.user.CreateUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final IUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @ResultTransaction
    @TrackExecutionTime("user.create")
    public Result<UserModel> create(UserModel user) {
        try {
            user.setPasswordHash(passwordEncoder.encode(user.getPasswordHash()));

            var created = repository.insert(user);

            return Result.success(created, 201);
        } catch (DataIntegrityViolationException e) {

            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_users_email")) {
                return  Result.failure(
                        "Email already exists",
                        409
                );
            }

            if (message != null && message.contains("uk_users_name")) {
                return  Result.failure(
                        "Username already exists",
                        409
                );
            }

            return  Result.failure(
                    "Database integrity error: " + message,
                    400
            );

        } catch (Exception e) {
            throw new InternalServerErrorException(
                    e.getMessage()
            );
        }
    }

}
