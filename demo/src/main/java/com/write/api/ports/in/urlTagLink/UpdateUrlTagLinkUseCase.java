package com.write.api.ports.in.urlTagLink;

import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.shared.validation.snowflake.IsId;

public interface UpdateUrlTagLinkUseCase {
    Result<UrlTagLinkModel> execute(UpdateUrlTagLinkDTO dto, @IsId Long id);
}
