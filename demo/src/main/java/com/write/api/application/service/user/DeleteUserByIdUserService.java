package com.write.api.application.service.user;

import com.write.api.application.shared.Result;
import com.write.api.ports.in.user.DeleteByIdUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUserByIdUserService implements DeleteByIdUserUseCase {

    private final IUserRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> deleteById(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure("User not found", 404);
        }

        return Result.success(200);
    }

}
