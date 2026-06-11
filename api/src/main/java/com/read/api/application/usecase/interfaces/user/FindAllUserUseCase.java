package com.read.api.application.usecase.interfaces.user;

import com.read.api.api.dto.user.UserFilter;
import com.read.api.domain.model.UserModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FindAllUserUseCase {
    Page<UserModel> execute(UserFilter filter, Pageable pageable);
}
