package com.write.api.application.service.apiKey;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.ports.in.apiKey.DeleteApiKeyUseCase;
import com.write.api.ports.out.repository.IApiKeyRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteApiKeyService implements DeleteApiKeyUseCase {

    IApiKeyRepository repository;
    IUserRoleRepository userRoleRepository;

    @Override
    @ResultTransaction
    @TrackExecutionTime("apikey.delete")
    public Result<Void> execute(Long id, Long userId) {
        List<String> role = userRoleRepository.findRoleByUserId(userId);

        boolean isAdmin = role.contains("ADMIN") || role.contains("SUPER_ADMIN");

        if (!isAdmin) {
            return Result.failure(
                    "Only ADMIN or SUPER_ADMIN can perform this action",
                    403
            );
        }

        int deleted = repository.deleteById(id);

        if (deleted == 0)
            return Result.failure(404, "Api key not found");

        return Result.success();
    }
}
