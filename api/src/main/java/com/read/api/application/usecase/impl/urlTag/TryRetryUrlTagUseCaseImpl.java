package com.read.api.application.usecase.impl.urlTag;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.base.AbstractRetryDeadLetterUseCase;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.urlTag.DeleteUrlTagByIdUseCase;
import com.read.api.application.usecase.interfaces.urlTag.InsertUrlTagUseCase;
import com.read.api.application.usecase.interfaces.urlTag.SaveUrlTagUseCase;
import com.read.api.application.usecase.interfaces.urlTag.TryRetryUrlTagUseCase;
import com.read.api.application.usecase.mapper.UrlTagServicesMapper;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UrlTagCdcEvent;
import com.read.api.domain.model.UrlTagModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;

@UseCase
public class TryRetryUrlTagUseCaseImpl
        extends AbstractRetryDeadLetterUseCase<UrlTagCdcEvent, UrlTagModel>
        implements TryRetryUrlTagUseCase {

    private final UrlTagServicesMapper mapper;
    private final DeleteUrlTagByIdUseCase delete;
    private final InsertUrlTagUseCase insert;
    private final SaveUrlTagUseCase save;

    public TryRetryUrlTagUseCaseImpl(
            DeadLetterEventRepository eventRepository,
            ObjectMapper objectMapper,
            UrlTagServicesMapper mapper,
            DeleteUrlTagByIdUseCase delete,
            InsertUrlTagUseCase insert,
            SaveUrlTagUseCase save
    ) {
        super(eventRepository, objectMapper);
        this.mapper = mapper;
        this.delete = delete;
        this.insert = insert;
        this.save = save;
    }

    @Override
    protected TypeReference<TiCdcEvent<UrlTagCdcEvent>> type() {
        return new TypeReference<>() {};
    }

    @Override
    protected UrlTagModel toModel(UrlTagCdcEvent event) {
        return mapper.toModel(event);
    }

    @Override
    protected Result<?> insert(UrlTagModel model) {
        return insert.execute(model);
    }

    @Override
    protected Result<?> save(UrlTagModel model) {
        return save.execute(model);
    }

    @Override
    protected Result<?> delete(Long id) {
        return delete.execute(id);
    }
}