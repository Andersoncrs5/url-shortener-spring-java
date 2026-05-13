package com.write.api.ports.in.user;

import com.write.api.application.shared.Result;

public interface DeleteByIdUserUseCase {
    Result<Void> deleteById(Long id);
}
