package com.write.api.application.service.role;

import com.write.api.application.dto.role.CreateRoleDTO;
import com.write.api.application.mapper.role.CreateRoleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.ports.in.role.CreateRoleUseCase;
import com.write.api.ports.out.repository.IRoleRepository;
import com.write.api.shared.db.DatabaseConstraintHandler;
import com.write.api.shared.tx.ResultTransaction;
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
public class CreateRoleService implements CreateRoleUseCase {
    IRoleRepository repository;
    CreateRoleMapper mapper;

    @Override
    @ResultTransaction
    public Result<RoleModel> execute(CreateRoleDTO dto) {
        RoleModel model = mapper.toModel(dto);

        try {
            RoleModel inserted = repository.insert(model);

            return Result.success(inserted, 201);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            if (message.contains("uk_roles_name")) {
                return  Result.failure(
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
