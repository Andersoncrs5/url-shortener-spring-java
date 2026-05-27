package com.write.api.application.service.urlTagLink;

import com.write.api.application.dto.urlTagLink.UpdateUrlTagLinkDTO;
import com.write.api.application.mapper.urlTagLink.UpdateUrlTagLinkMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlTagLinkModel;
import com.write.api.ports.in.urlTagLink.UpdateUrlTagLinkUseCase;
import com.write.api.ports.out.repository.IUrlTagLinkRepository;
import com.write.api.shared.db.DatabaseConstraintHandler;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UpdateUrlTagLinkService implements UpdateUrlTagLinkUseCase {

    UpdateUrlTagLinkMapper mapper;
    IUrlTagLinkRepository repository;

    @Override
    @ResultTransaction
    public Result<UrlTagLinkModel> execute(UpdateUrlTagLinkDTO dto, Long id) {
        UrlTagLinkModel link = this.repository.findById(id).orElse(null);
        if (link == null) return Result.failure(404, "Url Tag Link not found");

        this.mapper.update(dto, link);

        try {
            UrlTagLinkModel saved = repository.save(link);

            return Result.success(saved, 200);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return DatabaseConstraintHandler.handle(e);
            }

            if (message.contains("uk_url_tag_links_unique")) {
                return  Result.failure(
                        "Tag already present in url",
                        409
                );
            }

            if (message.contains("fk_url_tag_links_url")) {
                return Result.failure(
                        "Url not found",
                        404
                );
            }

            if (message.contains("fk_url_tag_links_tag")) {
                return Result.failure(
                        "Tag not found",
                        404
                );
            }

            if (message.contains("fk_url_tag_links_created_by")) {
                return Result.failure(
                        "User not found",
                        404
                );
            }

            return DatabaseConstraintHandler.handle(e);
        } catch (Exception e) {
            throw new InternalServerErrorException(
                    e.getMessage()
            );
        }

    }

}
