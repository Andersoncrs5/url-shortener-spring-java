package com.write.api.bootstrap;

import com.write.api.application.dto.userRole.CreateUserRoleDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.RoleModel;
import com.write.api.core.domain.model.UserModel;
import com.write.api.core.domain.model.UserRoleModel;
import com.write.api.core.domain.service.SnowflakeIdGenerator;
import com.write.api.infrastructure.config.properties.SuperAdminProperties;
import com.write.api.ports.in.userRole.CreateUserRoleUseCase;
import com.write.api.ports.out.repository.IRoleRepository;
import com.write.api.ports.out.repository.IUserRepository;
import com.write.api.ports.out.repository.IUserRoleRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;

@Slf4j
@Order(3)
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class LinkSuperAdmRoleToSuperAdmBootstrap implements ApplicationRunner {

    IUserRepository userRepository;
    IRoleRepository roleRepository;
    SuperAdminProperties properties;
    IUserRoleRepository userRoleRepository;
    SnowflakeIdGenerator generator;

    @Override
    public void run(ApplicationArguments args) {
        var roleName = "SUPER_ADMIN";
        UserModel superAdm = userRepository
                .findByEmailIgnoreCase(properties.getEmail())
                .orElse(null);

        if (superAdm == null) {
            log.error(
                    "Super admin user '{}' not found",
                    properties.getEmail()
            );
            return;
        }

        RoleModel role = roleRepository
                .findByNameIgnoreCase(roleName)
                .orElse(null);

        if (role == null) {
            log.error(
                    "Role 'SUPER_ADMIN' not found"
            );
            return;
        }


        if (userRoleRepository.existsByRoleIdAndUserId(role.getId(), superAdm.getId())) {
            log.info("Role already linked the user");
            return;
        }

        UserRoleModel userRole = new UserRoleModel();

        userRole.setId(generator.nextId());
        userRole.setRoleId(role.getId());
        userRole.setAssignedByUserId(superAdm.getId());
        userRole.setUserId(superAdm.getId());

        userRoleRepository.insert(userRole);
        List<String> roles = userRoleRepository.findRoleByUserId(superAdm.getId());

        UserModel saved = this.userRepository.save(superAdm);


    }
}