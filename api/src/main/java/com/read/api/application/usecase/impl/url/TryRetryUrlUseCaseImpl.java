package com.read.api.application.usecase.impl.url;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.base.AbstractRetryDeadLetterUseCase;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.url.DeleteUrlByIdUseCase;
import com.read.api.application.usecase.interfaces.url.InsertUrlUseCase;
import com.read.api.application.usecase.interfaces.url.SaveUrlUseCase;
import com.read.api.application.usecase.interfaces.url.TryRetryUrlUseCase;
import com.read.api.application.usecase.mapper.UrlServicesMapper;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlCdcEvent;
import com.read.api.domain.model.UrlModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;

@UseCase
public class TryRetryUrlUseCaseImpl
        extends AbstractRetryDeadLetterUseCase<UrlCdcEvent, UrlModel>
        implements TryRetryUrlUseCase {

    private final UrlServicesMapper mapper;
    private final DeleteUrlByIdUseCase delete;
    private final InsertUrlUseCase insert;
    private final SaveUrlUseCase save;

    public TryRetryUrlUseCaseImpl(
            DeadLetterEventRepository eventRepository,
            ObjectMapper objectMapper,
            UrlServicesMapper mapper,
            DeleteUrlByIdUseCase delete,
            InsertUrlUseCase insert,
            SaveUrlUseCase save
    ) {
        super(eventRepository, objectMapper);
        this.mapper = mapper;
        this.delete = delete;
        this.insert = insert;
        this.save = save;
    }

    @Override
    protected TypeReference<TiCdcEvent<UrlCdcEvent>> type() {
        return new TypeReference<>() {};
    }

    @Override
    protected UrlModel toModel(UrlCdcEvent event) {
        return mapper.toModel(event);
    }

    @Override
    protected Result<?> insert(UrlModel model) {
        return insert.execute(model);
    }

    @Override
    protected Result<?> save(UrlModel model) {
        return save.execute(model);
    }

    @Override
    protected Result<?> delete(Long id) {
        return delete.execute(id);
    }
}