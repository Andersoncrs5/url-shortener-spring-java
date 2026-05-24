package com.write.api.application.service.url;

import com.write.api.application.dto.url.CreateUrlDTO;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.url.CreateUrlUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUrlService implements CreateUrlUseCase {

    @Override
    public Result<UrlModel> execute(CreateUrlDTO dto, Long userId) {
        return null;
    }


}
