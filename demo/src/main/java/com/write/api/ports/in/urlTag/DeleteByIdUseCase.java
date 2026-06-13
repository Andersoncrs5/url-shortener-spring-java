package com.write.api.ports.in.urlTag;

import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface DeleteByIdUseCase {
    Result<Void> execute(@IsId Long id);
}
