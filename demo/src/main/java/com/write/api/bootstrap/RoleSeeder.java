package com.write.api.bootstrap;

import com.write.api.core.domain.model.RoleModel;
import com.write.api.ports.out.repository.IRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Order(1)
@Component
@RequiredArgsConstructor
public class RoleSeeder implements ApplicationRunner {

    private final IRoleRepository repository;

    @Override
    @Transactional
    public void run(@NonNull ApplicationArguments args) {

        createIfNotExists(
                "SUPER_ADMIN",
                "Super administrator"
        );

        createIfNotExists(
                "ADMIN",
                "System administrator"
        );

        createIfNotExists(
                "USER",
                "Default user"
        );

        createIfNotExists(
                "VIEWER",
                "Viewer of URLs"
        );
    }

    private void createIfNotExists(
            String name,
            String description
    ) {
        if (repository.existsByNameIgnoreCase(name)) {
            log.info("{} already exists", "Role " + name);
            return;
        }

        RoleModel role = new RoleModel();
        role.setName(name);
        role.setDescription(description);
        role.setActive(true);

        RoleModel inserted = repository.insert(role);
        log.info("Role {} created with success", inserted.getName());
    }
}