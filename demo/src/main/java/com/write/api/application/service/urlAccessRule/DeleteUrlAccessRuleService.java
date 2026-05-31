package com.write.api.application.service.urlAccessRule;

import com.write.api.application.shared.Result;
import com.write.api.ports.in.urlAccessRule.DeleteUrlAccessRuleUseCase;
import com.write.api.ports.out.repository.IUrlAccessRuleRepository;
import com.write.api.shared.tx.ResultTransaction;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Slf4j
@Service
@Validated
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class DeleteUrlAccessRuleService implements DeleteUrlAccessRuleUseCase {

    IUrlAccessRuleRepository repository;

    @Override
    @ResultTransaction
    public Result<Void> execute(Long id) {
        int deleted = repository.deleteById(id);

        if (deleted == 0) {
            return Result.failure(404, "Url Access Rule not found");
        }

        return Result.success();
    }
}
