package com.write.api.ports.in.urlTagLink;

import com.write.api.application.dto.urlTagLink.CreateUrlTagLinkDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface CreateUrlTagLinkUseCase {
    Result<UrlTagLinkModel> execute(CreateUrlTagLinkDTO dto, @IsId Long userId);
}
