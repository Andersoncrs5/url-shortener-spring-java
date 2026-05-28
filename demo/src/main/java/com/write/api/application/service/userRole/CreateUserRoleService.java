package com.write.api.application.service.userRole;

import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.mapper.userRole.CreateUserRoleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.ports.in.userRole.CreateUserRoleUseCase;
import com.write.api.ports.out.repository.IUserRoleRepository;
import com.write.api.shared.db.DatabaseConstraintHandler;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CreateUserRoleService implements CreateUserRoleUseCase {
    IUserRoleRepository repository;
    CreateUserRoleMapper mapper;

    @Override
    @ResultTransaction
    public Result<UserRoleModel> execute(
            CreateUserRoleDTO dto,
            Long assignedByUserId
    ) {
        UserRoleModel model = mapper.toModel(dto);
        model.setAssignedByUserId(assignedByUserId);

        try {
            UserRoleModel inserted = repository.insert(model);

            return Result.success(inserted, 201);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            if (message.contains("uk_user_roles_user_role")) {
                return Result.failure(
                        "User already has this role",
                        409
                );
            }

            if (message.contains("fk_user_roles_user_id")) {
                return Result.failure(
                        "User not found",
                        404
                );
            }

            if (message.contains("fk_user_roles_role_id")) {
                return Result.failure(
                        "Role not found",
                        404
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}

