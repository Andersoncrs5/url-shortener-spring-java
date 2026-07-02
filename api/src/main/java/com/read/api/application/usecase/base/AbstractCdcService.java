package com.read.api.application.usecase.base;

import com.read.api.domain.service.RedisCrudService;
import com.read.api.utils.result.Result;

public abstract class AbstractCdcService<TCdc, TModel> {

    protected abstract Result<TModel> insert(TModel model);

    protected abstract Result<TModel> update(TModel model);

    protected abstract Result<Void> delete(Long id);

    protected abstract TModel toModel(TCdc event);

    protected abstract RedisCrudService redis();

    protected abstract String metricName();
}
