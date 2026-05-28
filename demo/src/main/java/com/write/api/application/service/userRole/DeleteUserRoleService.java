package com.write.api.application.service.userRole;

import com.write.api.application.shared.Result;
import com.write.api.ports.in.userRole.DeleteUserRoleUseCase;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUserRoleService implements DeleteUserRoleUseCase {

    IUserRoleRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> deleteById(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure("User role not found", 404);
        }

        return Result.success(200);
    }

}
