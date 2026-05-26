package com.write.api.application.service.url;

import com.write.api.application.dto.url.UpdateUrlDTO;
import com.write.api.application.mapper.url.UpdateUrlMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.exception.InternalServerErrorException;
import com.write.api.core.domain.model.UrlModel;
import com.write.api.ports.in.url.UpdateUrlUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateUrlService implements UpdateUrlUseCase {

    private final IUrlRepository repository;
    private final UpdateUrlMapper mapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @ResultTransaction
    public Result<UrlModel> execute(Long id, UpdateUrlDTO dto) {
        UrlModel url = repository.findById(id).orElse(null);

        if (url == null) {
            return Result.failure(404, "Url not found");
        }

        mapper.update(dto, url);

        if (dto.password() != null) {
            url.setPasswordHash(passwordEncoder.encode(dto.password()));
        }

        try {
            UrlModel save = repository.save(url);

            return Result.success(save, 200);
        } catch (DataIntegrityViolationException e) {
            String message = e.getMostSpecificCause().getMessage();

            if (message == null) {
                return Result.failure(
                        "Database integrity error",
                        400
                );
            }

            if (message.contains("uk_urls_short_code")) {
                return  Result.failure(
                        "Short code " + url.getShortCode() + " already exists",
                        409
                );
            }
            if (message.contains("fk_urls_user")) {
                return Result.failure(
                        "User not found",
                        404
                );
            }

            if (message.contains("cannot be null")) {
                return Result.failure(
                        "Required field is missing",
                        400
                );
            }

            if (message.contains("Data too long")) {
                return Result.failure(
                        "One of the fields exceeded the allowed size",
                        400
                );
            }

            return  Result.failure(
                    "Database integrity error: " + message,
                    400
            );
        } catch (Exception e) {
            throw new InternalServerErrorException(e.getMessage());
        }
    }


}
