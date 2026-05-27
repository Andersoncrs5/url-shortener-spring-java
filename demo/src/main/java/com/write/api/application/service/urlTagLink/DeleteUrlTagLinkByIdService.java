package com.write.api.application.service.urlTagLink;

import com.write.api.application.shared.Result;
import com.write.api.ports.in.urlTagLink.DeleteUrlTagLinkByIdUseCase;
import com.write.api.ports.out.repository.IUrlTagLinkRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUrlTagLinkByIdService implements DeleteUrlTagLinkByIdUseCase {
    private final IUrlTagLinkRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Url Tag link not found");
        }

        return Result.success();
    }

}
