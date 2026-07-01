package com.read.api.application.usecase.impl.role;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.read.api.application.usecase.base.AbstractRetryDeadLetterUseCase;
import com.read.api.application.usecase.base.UseCase;
import com.read.api.application.usecase.interfaces.role.DeleteRoleByIdUseCase;
import com.read.api.application.usecase.interfaces.role.InsertRoleUseCase;
import com.read.api.application.usecase.interfaces.role.SaveRoleUseCase;
import com.read.api.application.usecase.interfaces.role.TryRetryRoleUseCase;
import com.read.api.application.usecase.mapper.RoleServicesMapper;
import com.read.api.domain.cdc.TiCdcEvent;
import com.read.api.domain.cdc.classes.RoleCdcEvent;
import com.read.api.domain.model.RoleModel;
import com.read.api.domain.repository.DeadLetterEventRepository;
import com.read.api.utils.result.Result;

@UseCase
public class TryRetryRoleUseCaseImpl
        extends AbstractRetryDeadLetterUseCase<
                RoleCdcEvent,
                RoleModel>
        implements TryRetryRoleUseCase {

    private final RoleServicesMapper mapper;
    private final InsertRoleUseCase insert;
    private final SaveRoleUseCase save;
    private final DeleteRoleByIdUseCase delete;

    protected TryRetryRoleUseCaseImpl(DeadLetterEventRepository eventRepository, ObjectMapper objectMapper, RoleServicesMapper mapper, InsertRoleUseCase insert, SaveRoleUseCase save, DeleteRoleByIdUseCase delete) {
        super(eventRepository, objectMapper);
        this.mapper = mapper;
        this.insert = insert;
        this.save = save;
        this.delete = delete;
    }

    @Override
    protected TypeReference<TiCdcEvent<RoleCdcEvent>> type() {
        return new TypeReference<>() {};
    }

    @Override
    protected RoleModel toModel(RoleCdcEvent event) {
        return mapper.toModel(event);
    }

    @Override
    protected Result<?> insert(RoleModel model) {
        return insert.execute(model);
    }

    @Override
    protected Result<?> save(RoleModel model) {
        return save.execute(model);
    }

    @Override
    protected Result<?> delete(Long id) {
        return delete.execute(id);
    }
}