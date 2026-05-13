package com.write.api.application.service.user;

import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.user.CreateUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final IUserRepository repository;

    @Override
    public Result<UserModel> create(UserModel user) {
        try {
            var created = repository.insert(user);

            return Result.success(created, 201);
        } catch (DataIntegrityViolationException e) {

            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_user_email")) {
                return  Result.failure(
                        "Email already exists",
                        409
                );
            }

            if (message != null && message.contains("uk_user_name")) {
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
