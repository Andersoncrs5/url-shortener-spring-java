package com.read.api.domain.repository;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.base.BaseRepository;

import java.util.Optional;

public interface UserRepository extends BaseRepository<UserModel, Long, UserFilter> {
    Optional<UserModel> findByEmailIgnoreCase(String email);
    boolean existsByEmailIgnoreCase(String email);
}
