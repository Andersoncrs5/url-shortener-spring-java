package com.write.api.bootstrap;

import com.write.api.application.dto.auth.AuthTokenResponseDTO;
import com.write.api.application.dto.user.CreateUserDTO;
import com.write.api.application.shared.Result;
import com.write.api.infrastructure.config.properties.SuperAdminProperties;
import com.write.api.ports.in.auth.RegisterUserUseCase;
import com.write.api.ports.out.repository.IUserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Slf4j
@Order(2)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserSuperAdminBootstrap implements ApplicationRunner {

    RegisterUserUseCase registerUserUseCase;
    SuperAdminProperties properties;
    IUserRepository userRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (userRepository.existsByEmailIgnoreCase(properties.getEmail())) {
            log.info("Super admin already exists");
            return;
        }

        CreateUserDTO dto = new CreateUserDTO(
                properties.getName(),
                properties.getEmail(),
                properties.getPassword()
        );

        Result<AuthTokenResponseDTO> result =
                registerUserUseCase.execute(dto);

        if (result.isSuccess()) {
            log.info(
                    "Super admin '{}' created successfully",
                    properties.getEmail()
            );
            return;
        }

        if (result.getStatusCode() == 409) {
            log.info(
                    "Super admin '{}' already exists",
                    properties.getEmail()
            );
            return;
        }

        log.error(
                "Failed to create super admin '{}': {}",
                properties.getEmail(),
                result.getMessage()
        );
    }
}