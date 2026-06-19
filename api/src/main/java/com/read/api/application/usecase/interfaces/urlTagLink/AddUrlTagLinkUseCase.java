package com.read.api.application.usecase.interfaces.urlTagLink;

import com.read.api.domain.model.UrlModel;
import com.read.api.utils.result.Result;
import com.read.api.utils.validation.isId.IsId;

public interface AddUrlTagLinkUseCase {
    Result<UrlModel> execute(@IsId Long urlId, @IsId Long tagId);
}
