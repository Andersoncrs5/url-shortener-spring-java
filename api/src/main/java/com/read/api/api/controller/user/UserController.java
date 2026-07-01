package com.read.api.api.controller.user;

import com.read.api.api.controller.base.RestApiController;
import com.read.api.api.dto.ResponseHTTP;
import com.read.api.api.dto.user.UserDTO;
import com.read.api.api.dto.user.UserFilter;
import com.read.api.application.usecase.interfaces.user.ExistsEmailUserUseCase;
import com.read.api.application.usecase.interfaces.user.ExistsUserByNameUseCase;
import com.read.api.application.usecase.interfaces.user.FindAllUserUseCase;
import com.read.api.application.usecase.interfaces.user.FindByIdUserUseCase;
import com.read.api.domain.model.UserModel;
import com.read.api.utils.annotation.ratelimit.RateLimited;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@RestApiController("v1/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController implements UserControllerDocs {

    FindAllUserUseCase findAll;
    FindByIdUserUseCase findByIdUser;
    ExistsEmailUserUseCase existsEmail;
    ExistsUserByNameUseCase existsName;
    UserMapperController mapper;

    @Override
    @RateLimited("read-low")
    public ResponseEntity<Page<?>> getAll(
            String idempotencyKey,
            UserFilter filter,
            UserPageRequestDTO page
    ) {
        Page<UserModel> result = findAll.execute(filter, page.toPageable());

        var items = result.map(mapper::toDTO);

        return ResponseEntity.ok(items);
    }

    @Override
    @RateLimited("read-strong")
    public ResponseEntity<ResponseHTTP<UserDTO>> findById(
            @PathVariable Long id,
            @RequestHeader("X-Idempotency-Key") String idempotencyKey
    ) {
        Result<UserModel> result = findByIdUser.execute(id);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHTTP.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(result.getStatusCode())
            .body(
                ResponseHTTP.success(
                        mapper.toDTO(result.getValue()),
                        "User found",
                        idempotencyKey
                )
            );

    }

    @Override
    @RateLimited("read-strong")
    public ResponseEntity<ResponseHTTP<Boolean>> emailExists(
            @RequestParam String email
    ) {
        boolean exists = existsEmail.execute(email);

        return ResponseEntity.ok(
                ResponseHTTP.success(
                        exists,
                        "Email checked"
                )
        );
    }

    @Override
    @RateLimited("read-strong")
    public ResponseEntity<ResponseHTTP<Boolean>> nameExists(
            @RequestParam String name
    ) {
        boolean exists = existsName.execute(name);

        return ResponseEntity.ok(
                ResponseHTTP.success(
                        exists,
                        "Name checked"
                )
        );
    }

}
