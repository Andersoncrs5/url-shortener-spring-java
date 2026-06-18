package com.read.api.application.usecase.interfaces.urlTag;

import com.read.api.utils.result.Result;
import com.read.api.utils.validation.isId.IsId;

public interface DeleteUrlTagByIdUseCase {
    Result<Void> execute(@IsId Long id);
}
