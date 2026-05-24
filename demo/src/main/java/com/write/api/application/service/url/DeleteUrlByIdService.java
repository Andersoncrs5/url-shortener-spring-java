package com.write.api.application.service.url;

import com.write.api.application.shared.Result;
import com.write.api.ports.in.url.DeleteUrlByIdUseCase;
import com.write.api.ports.out.repository.IUrlRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUrlByIdService implements DeleteUrlByIdUseCase {

    private final IUrlRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Url not found");
        }

        return Result.success();
    }

}
