package com.write.api.core.domain.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum OutboxStatusEnum {

    PENDING("Pendente"),
    PROCESSING("Processando"),
    PROCESSED("Processado"),
    RETRYING("Retrying"),
    FAILED("Falhou");

    private final String description;
}
