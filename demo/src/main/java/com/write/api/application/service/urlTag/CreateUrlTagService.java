package com.write.api.application.service.urlTag;

import com.write.api.application.dto.urlTag.CreateUrlTagDTO;
import com.write.api.application.mapper.urlTag.CreateUrlTagMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.ports.in.urlTag.CreateUrlTagUseCase;
import com.write.api.ports.out.repository.IUrlTagRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateUrlTagService implements CreateUrlTagUseCase {

    private final CreateUrlTagMapper mapper;
    private final IUrlTagRepository repository;

    @Override
    @ResultTransaction
    public Result<UrlTagModel> execute(CreateUrlTagDTO dto, Long userId) {
        UrlTagModel model = mapper.toModel(dto);
        model.setUserId(userId);

        if (dto.parentId() != null) {
            boolean exists = this.repository.existsById(dto.parentId());
            if (!exists) return Result.failure(404, "Url Tag not found");
        }

        try {
            var created = repository.insert(model);

            return Result.success(created, 201);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_url_tag_slug")) {
                return  Result.failure(
                        "Slug " + dto.slug() + " already exists",
                        409
                );
            }

            if (message != null && message.contains("uk_url_tag_name")) {
                return  Result.failure(
                        "Name " + dto.name() + " already exists",
                        409
                );
            }

            return  Result.failure(
                    "Database integrity error: " + message,
                    400
            );
        } catch (Exception e) {
            throw new InternalServerErrorException(
                    e.getMessage()
            );
        }
    }

}
