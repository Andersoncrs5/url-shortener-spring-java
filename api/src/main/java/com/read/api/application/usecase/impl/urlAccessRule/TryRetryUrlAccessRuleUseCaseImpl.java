package com.read.api.application.usecase.impl.urlAccessRule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.base.AbstractRetryDeadLetterUseCase;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.DeleteUrlAccessRuleByIdUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.InsertUrlAccessRuleUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.SaveUrlAccessRuleUseCase;
import com.read.api.application.usecase.interfaces.urlAccessRule.TryRetryUrlAccessRuleUseCase;
import com.read.api.application.usecase.mapper.UrlAccessRuleServicesMapper;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlAccessRuleCdcEvent;
import com.read.api.domain.model.UrlAccessRuleModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;

@UseCase
public class TryRetryUrlAccessRuleUseCaseImpl
        extends AbstractRetryDeadLetterUseCase<UrlAccessRuleCdcEvent, UrlAccessRuleModel>
        implements TryRetryUrlAccessRuleUseCase {

    private final UrlAccessRuleServicesMapper mapper;
    private final DeleteUrlAccessRuleByIdUseCase delete;
    private final InsertUrlAccessRuleUseCase insert;
    private final SaveUrlAccessRuleUseCase save;

    public TryRetryUrlAccessRuleUseCaseImpl(
            DeadLetterEventRepository eventRepository,
            ObjectMapper objectMapper,
            UrlAccessRuleServicesMapper mapper,
            DeleteUrlAccessRuleByIdUseCase delete,
            InsertUrlAccessRuleUseCase insert,
            SaveUrlAccessRuleUseCase save
    ) {
        super(eventRepository, objectMapper);
        this.mapper = mapper;
        this.delete = delete;
        this.insert = insert;
        this.save = save;
    }

    @Override
    protected TypeReference<TiCdcEvent<UrlAccessRuleCdcEvent>> type() {
        return new TypeReference<>() {};
    }

    @Override
    protected UrlAccessRuleModel toModel(UrlAccessRuleCdcEvent event) {
        return mapper.toModel(event);
    }

    @Override
    protected Result<?> insert(UrlAccessRuleModel model) {
        return insert.execute(model);
    }

    @Override
    protected Result<?> save(UrlAccessRuleModel model) {
        return save.execute(model);
    }

    @Override
    protected Result<?> delete(Long id) {
        return delete.execute(id);
    }
}