package com.write.api.application.service.urlTag;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.ports.in.urlTag.DeleteByIdUseCase;
import com.write.api.ports.out.repository.IUrlTagRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteByIdService implements DeleteByIdUseCase {

    private final IUrlTagRepository repository;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.tag.delete")
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Url Tag not found");
        }

        return Result.success();
    }

}
