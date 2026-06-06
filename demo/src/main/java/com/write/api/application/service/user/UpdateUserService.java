package com.write.api.application.service.user;

import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.application.mapper.user.UserUpdateMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.user.UpdateUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateUserService implements UpdateUserUseCase {

    IUserRepository repository;
    UserUpdateMapper mapper;
    PasswordEncoder passwordEncoder;

    @Override
    @ResultTransaction
    @TrackExecutionTime("user.update")
    public Result<UserModel> update(UserModel user, UpdateUserDTO dto) {
        mapper.updateUserFromDto(dto, user);

        if (dto.password() != null && !dto.password().isBlank()) {
            user.setPasswordHash(
                    passwordEncoder.encode(dto.password())
            );
        }

        UserModel updated = repository.save(user);

        return Result.success(updated, 200);
    }
}
