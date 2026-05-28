package com.write.api.application.service.role;

import com.write.api.application.dto.role.UpdateRoleDTO;
import com.write.api.application.mapper.role.UpdateRoleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.ports.in.role.UpdateRoleUseCase;
import com.write.api.ports.out.repository.IRoleRepository;
import com.write.api.shared.db.DatabaseConstraintHandler;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateRoleService implements UpdateRoleUseCase {
    IRoleRepository repository;
    UpdateRoleMapper mapper;

    @Override
    public Result<RoleModel> execute(Long id, UpdateRoleDTO dto) {
        RoleModel role = repository.findById(id).orElse(null);

        if (role == null) {
            return Result.failure("Role not found", 404);
        }

        mapper.merge(dto, role);

        try {
            RoleModel updated = repository.save(role);

            return Result.success(updated, 200);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            if (message.contains("uk_roles_name")) {
                return Result.failure(
                        "Name: '" + dto.name() + "' already exists",
                        409
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {
            throw new InternalServerErrorException(
                    e.getMessage()
            );
        }
    }

}
