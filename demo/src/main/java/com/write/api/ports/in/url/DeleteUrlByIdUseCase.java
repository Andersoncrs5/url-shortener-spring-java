package com.write.api.ports.in.url;

import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface DeleteUrlByIdUseCase {
    Result<Void> execute(@IsId Long id);
}
