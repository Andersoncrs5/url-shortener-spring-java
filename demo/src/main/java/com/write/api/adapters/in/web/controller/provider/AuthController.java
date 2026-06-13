package com.write.api.adapters.in.web.controller.provider;

import com.write.api.adapters.in.web.controller.docs.AuthControllerDocs;
import com.write.api.adapters.in.web.shared.response.ResponseHttp;
import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.dto.user.LoginUserDTO;
import com.write.api.application.shared.Result;
import com.write.api.infrastructure.config.api.idempotent.Idempotent;
import com.write.api.infrastructure.config.security.classes.UserPrincipal;
import com.write.api.core.domain.model.UserModel;
import com.write.api.ports.in.auth.LoginUserUseCase;
import com.write.api.ports.in.auth.LogoutAuthUseCase;
import com.write.api.ports.in.auth.RefreshTokenUseCase;
import com.write.api.ports.in.auth.RegisterUserUseCase;
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
@RequestMapping("v1/auth")
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthController implements AuthControllerDocs {

    LoginUserUseCase loginUserUseCase;
    RegisterUserUseCase registerUserUseCase;
    LogoutAuthUseCase logoutAuthService;
    RefreshTokenUseCase refreshTokenUseCase;

    @Idempotent
    public ResponseEntity<ResponseHttp<?>> logout(
            String idempotencyKey,
            UserPrincipal principal
    ) {
        Result<UserModel> result = logoutAuthService.execute(principal.getId());

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.ok(
                ResponseHttp.success(null, "Logout success", idempotencyKey)
        );
    }

    @Override
    public ResponseEntity<ResponseHttp<AuthTokenResponseDTO>> login(
            LoginUserDTO dto,
            String idempotencyKey
    ) {
        Result<AuthTokenResponseDTO> result = loginUserUseCase.login(dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.ok(
                ResponseHttp.success(result.getValue(), "Login success", idempotencyKey)
        );
    }

    @Override
    public ResponseEntity<ResponseHttp<AuthTokenResponseDTO>> register(
            CreateUserDTO dto,
            String idempotencyKey
    ) {
        Result<AuthTokenResponseDTO> result = registerUserUseCase.execute(dto);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity.status(201)
                .body(ResponseHttp.success(result.getValue(), "User created", idempotencyKey));
    }

    @Override
    public ResponseEntity<ResponseHttp<?>> refreshToken(
            String refreshToken,
            String idempotencyKey
    ) {
        Result<AuthTokenResponseDTO> result = this.refreshTokenUseCase.execute(refreshToken);

        if (result.isFailure()) {
            return ResponseEntity
                    .status(result.getStatusCode())
                    .body(ResponseHttp.error(result.getMessage(), idempotencyKey));
        }

        return ResponseEntity
            .status(200)
            .body(
                ResponseHttp.success(
                    result.getValue(),
                    "Tokens created",
                    idempotencyKey
                )
            );
    }

}