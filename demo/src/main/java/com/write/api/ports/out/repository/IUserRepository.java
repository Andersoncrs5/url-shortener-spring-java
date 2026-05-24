package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UserModel;

import java.util.Optional;

public interface IUserRepository extends CrudRepository<UserModel, Long> {
    boolean existsByEmailIgnoreCase(String email);
    Optional<UserModel> findByEmailIgnoreCase(String email);
    Optional<UserModel> findByRefreshToken(String refreshToken);
}
