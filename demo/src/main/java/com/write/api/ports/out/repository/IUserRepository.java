package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UserModel;

import java.util.Optional;

public interface IUserRepository {
    UserModel save(UserModel user);
    UserModel insert(UserModel user);
    int deleteById(Long id);
    boolean existsByEmailIgnoreCase(String email);
    Optional<UserModel> findByEmailIgnoreCase(String email);
    Optional<UserModel> findById(Long id);
}
