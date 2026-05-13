package com.write.api.ports.out.repository;

import com.write.api.core.domain.model.UserModel;

public interface IUserRepository {
    UserModel save(UserModel user);
    UserModel insert(UserModel user);
    int deleteById(Long id);
}
