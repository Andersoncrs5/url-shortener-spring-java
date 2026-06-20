package com.read.api.application.usecase.impl.userRole;

import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.userRole.RemoveUserRoleLinkUseCase;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.RoleRepository;
import com.read.api.domain.repository.UserRepository;
import com.read.api.utils.result.Result;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

@UseCase
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RemoveUserRoleLinkUseCaseImpl implements RemoveUserRoleLinkUseCase {

    UserRepository userRepository;
    RoleRepository roleRepository;

    @Override
    public Result<UserModel> execute(Long userId, Long roleId) {

        // 1. Valida se o Usuário existe
        UserModel user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return Result.failure(
                    "User not found",
                    404
            );
        }

        // 2. Valida se a Role existe
        RoleModel role = roleRepository.findById(roleId).orElse(null);
        if (role == null) {
            return Result.failure(
                    "Role not found",
                    404
            );
        }

        // 3. Se o usuário NÃO tiver essa Role, retorna sucesso imediatamente (Idempotência)
        if (!user.getRoles().contains(role.getName())) {
            return Result.success(user, 200);
        }

        // 4. Remove a Role do Usuário
        user.getRoles().remove(role.getName());

        // 5. Salva as alterações no banco de dados
        UserModel saved = userRepository.save(user);

        return Result.success(saved, 200);
    }
}