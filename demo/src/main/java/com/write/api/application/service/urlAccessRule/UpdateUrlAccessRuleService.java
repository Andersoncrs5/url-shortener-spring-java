package com.write.api.application.service.urlAccessRule;

import com.write.api.application.dto.urlAccessRule.UpdateUrlAccessRuleDTO;
import com.write.api.application.mapper.urlAccessRule.UpdateUrlAccessRuleMapper;
import com.write.api.application.shared.Result;
import com.write.api.core.domain.model.UrlAccessRuleModel;
import com.write.api.ports.in.urlAccessRule.UpdateUrlAccessRuleUseCase;
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
public class UpdateUrlAccessRuleService implements UpdateUrlAccessRuleUseCase {

    IUrlAccessRuleRepository repository;
    UpdateUrlAccessRuleMapper mapper;

    @Override
    @ResultTransaction
    public Result<UrlAccessRuleModel> execute(UpdateUrlAccessRuleDTO dto, Long id) {
        var rule = this.repository.findById(id).orElse(null);

        if (rule == null) return Result.failure("Url Access Rule not found", 404);

        mapper.update(dto, rule);

        UrlAccessRuleModel saved = repository.save(rule);

        return Result.success(saved, 200);
    }

}
