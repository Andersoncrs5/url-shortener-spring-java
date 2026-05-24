package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.UserControllerDocs;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.user.UpdateUserDTO;
import com.write.api.application.mapper.user.UserUpdateMapper;
import com.write.api.application.shared.Result;
import com.write.api.config.security.classes.UserPrincipal;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.user.DeleteByIdUserUseCase;
import com.write.api.ports.in.user.UpdateUserUseCase;
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
@RequestMapping("v1/user")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController implements UserControllerDocs {

    DeleteByIdUserUseCase deleteByIdUserUseCase;
    UpdateUserUseCase updateUserUseCase;
    UserUpdateMapper mapper;

    @Override
    public ResponseEntity<ResponseHttp<?>> delete(
            String idempotencyKey,
            UserPrincipal principal
    ) {
        var result = this.deleteByIdUserUseCase.deleteById(principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(ResponseHttp.success(null, "User deleted", idempotencyKey));
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> update(
            String idempotencyKey,
            UserPrincipal principal,
            UpdateUserDTO dto
    ) {
        Result<UserModel> result = updateUserUseCase.update(principal.getUser(), dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
                .body(ResponseHttp.success(mapper.toDTO(result.getValue()), "User updated", idempotencyKey));
    }



}
