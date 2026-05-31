package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.UserRoleControllerDocs;
import com.write.api.adapters.in.web.mapper.UserRoleMapper;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.ports.in.userRole.CreateUserRoleUseCase;
import com.write.api.ports.in.userRole.DeleteUserRoleUseCase;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("v1/user-role")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserRoleController implements UserRoleControllerDocs {

    UserRoleMapper mapper;
    CreateUserRoleUseCase createUserRole;
    DeleteUserRoleUseCase deleteUserRole;

    @Override
    public ResponseEntity<ResponseHttp<?>> create(
            CreateUserRoleDTO dto,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<UserRoleModel> result = createUserRole.execute(dto, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                mapper.toDTO(result.getValue()),
                                "Role added",
                                idempotencyKey
                        )
                );
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> delete(
            Long id,
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<Void> result = deleteUserRole.deleteById(id, principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(
                        ResponseHttp.success(
                                null,
                                "Role removed",
                                idempotencyKey
                        )
                );
    }

}
