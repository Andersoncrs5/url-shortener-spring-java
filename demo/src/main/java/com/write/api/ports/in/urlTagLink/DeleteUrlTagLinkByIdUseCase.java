package com.write.api.ports.in.urlTagLink;

import com.write.api.application.shared.Result;
import com.write.api.shared.validation.snowflake.IsId;

public interface DeleteUrlTagLinkByIdUseCase {
    Result<Void> execute(@IsId Long id);
}
