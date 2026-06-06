package com.write.api.application.service.urlRedirectRule;

import com.write.api.application.shared.Result;
import com.write.api.application.shared.annotations.TrackExecutionTime;
import com.write.api.ports.in.urlRedirectRule.DeleteUrlRedirectRuleUseCase;
import com.write.api.ports.out.repository.IUrlRedirectRuleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlRedirectRuleService implements DeleteUrlRedirectRuleUseCase {
    IUrlRedirectRuleRepository repository;

    @Override
    @ResultTransaction
    @TrackExecutionTime("url.redirect.delete")
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Url Rule not found");
        }

        return Result.success();
    }

}
