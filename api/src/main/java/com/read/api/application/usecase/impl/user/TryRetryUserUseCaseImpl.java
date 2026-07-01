package com.read.api.application.usecase.impl.user;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.base.AbstractRetryDeadLetterUseCase;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.user.DeleteByIdUserUseCase;
import com.read.api.application.usecase.interfaces.user.InsertUserUseCase;
import com.read.api.application.usecase.interfaces.user.SaveUserUseCase;
import com.read.api.application.usecase.interfaces.user.TryRetryUserUseCase;
import com.read.api.application.usecase.mapper.UserServicesMapper;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.UserCdcEvent;
import com.read.api.domain.model.UserModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;

@UseCase
public class TryRetryUserUseCaseImpl
        extends AbstractRetryDeadLetterUseCase<UserCdcEvent, UserModel>
        implements TryRetryUserUseCase {

    private final UserServicesMapper mapper;
    private final DeleteByIdUserUseCase delete;
    private final InsertUserUseCase insert;
    private final SaveUserUseCase save;

    public TryRetryUserUseCaseImpl(
            DeadLetterEventRepository eventRepository,
            ObjectMapper objectMapper,
            UserServicesMapper mapper,
            DeleteByIdUserUseCase delete,
            InsertUserUseCase insert,
            SaveUserUseCase save
    ) {
        super(eventRepository, objectMapper);
        this.mapper = mapper;
        this.delete = delete;
        this.insert = insert;
        this.save = save;
    }

    @Override
    protected TypeReference<TiCdcEvent<UserCdcEvent>> type() {
        return new TypeReference<>() {};
    }

    @Override
    protected UserModel toModel(UserCdcEvent event) {
        return mapper.toModel(event);
    }

    @Override
    protected Result<?> insert(UserModel model) {
        return insert.execute(model);
    }

    @Override
    protected Result<?> save(UserModel model) {
        return save.execute(model);
    }

    @Override
    protected Result<?> delete(Long id) {
        return delete.execute(id);
    }
}