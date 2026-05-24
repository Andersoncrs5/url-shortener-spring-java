package com.write.api.ports.in.urlTag;

import com.write.api.application.shared.Result;

public interface DeleteByIdUseCase {
    Result<Void> execute(Long id);
}
