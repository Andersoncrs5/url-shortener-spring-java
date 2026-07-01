package com.read.api.application.usecase.impl.urlRedirectRule;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.base.AbstractRetryDeadLetterUseCase;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.DeleteUrlRedirectRuleByIdUseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.InsertUrlRedirectRuleUseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.SaveUrlRedirectRuleUseCase;
import com.read.api.application.usecase.interfaces.urlRedirectRule.TryRetryUrlRedirectRuleUseCase;
import com.read.api.application.usecase.mapper.UrlRedirectRuleServicesMapper;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlRedirectRuleCdcEvent;
import com.read.api.domain.model.UrlRedirectRuleModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;

@UseCase
public class TryRetryUrlRedirectRuleUseCaseImpl
        extends AbstractRetryDeadLetterUseCase<UrlRedirectRuleCdcEvent, UrlRedirectRuleModel>
        implements TryRetryUrlRedirectRuleUseCase {

    private final UrlRedirectRuleServicesMapper mapper;
    private final DeleteUrlRedirectRuleByIdUseCase delete;
    private final InsertUrlRedirectRuleUseCase insert;
    private final SaveUrlRedirectRuleUseCase save;

    public TryRetryUrlRedirectRuleUseCaseImpl(
            DeadLetterEventRepository eventRepository,
            ObjectMapper objectMapper,
            UrlRedirectRuleServicesMapper mapper,
            DeleteUrlRedirectRuleByIdUseCase delete,
            InsertUrlRedirectRuleUseCase insert,
            SaveUrlRedirectRuleUseCase save
    ) {
        super(eventRepository, objectMapper);
        this.mapper = mapper;
        this.delete = delete;
        this.insert = insert;
        this.save = save;
    }

    @Override
    protected TypeReference<TiCdcEvent<UrlRedirectRuleCdcEvent>> type() {
        return new TypeReference<>() {};
    }

    @Override
    protected UrlRedirectRuleModel toModel(UrlRedirectRuleCdcEvent event) {
        return mapper.toModel(event);
    }

    @Override
    protected Result<?> insert(UrlRedirectRuleModel model) {
        return insert.execute(model);
    }

    @Override
    protected Result<?> save(UrlRedirectRuleModel model) {
        return save.execute(model);
    }

    @Override
    protected Result<?> delete(Long id) {
        return delete.execute(id);
    }
}