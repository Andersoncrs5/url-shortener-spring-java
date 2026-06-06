package com.write.api.application.service.urlTag;

import com.write.api.application.dto.urlTag.UpdateUrlTagDTO;
import com.write.api.application.mapper.urlTag.UpdateUrlTagMapper;
import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlTagModel;
import com.write.api.ports.in.urlTag.UpdateUrlTagUseCase;
import com.write.api.ports.out.repository.IUrlTagRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service @RequiredArgsConstructor
public class UpdateUrlTagService implements UpdateUrlTagUseCase {

    private final IUrlTagRepository repository;
    private final UpdateUrlTagMapper mapper;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.tag.update")
    public Result<UrlTagModel> execute(Long id, UpdateUrlTagDTO dto) {
        UrlTagModel tag = repository.findById(id)
                .orElse(null);

        if (tag == null) {
            return Result.failure(404, "Url Tag not found");
        }

        if (dto.parentId() != null) {

            if (dto.parentId().equals(id)) {
                return Result.failure(409, "Parent Id tag conflict");
            }

            boolean parentExists = repository.existsById(dto.parentId());

            if (!parentExists) {
                return Result.failure(404, "Parent Url Tag not found");
            }
        }

        mapper.updateModelFromDto(dto, tag);

        try {
            UrlTagModel updated = repository.save(tag);

            return Result.success(updated, 200);

        } catch (DataIntegrityViolationException e) {

            String message = e.getMostSpecificCause().getMessage();

            if (message != null && message.contains("uk_url_tag_slug")) {
                return Result.failure(
                        "Slug " + dto.slug() + " already exists",
                        409
                );
            }

            if (message != null && message.contains("uk_url_tag_name")) {
                return Result.failure(
                        "Name " + dto.name() + " already exists",
                        409
                );
            }

            return Result.failure(
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