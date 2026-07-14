package com.read.api.domain.cdc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.read.api.domain.enums.TiCdcEventTypeEnum;
import com.read.api.infrastructure.config.jackson.Boolean01Deserializer;
import com.read.api.utils.validation.isId.IsId;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TiCdcEvent<T>(
        @IsId
        Long id,
        String database,
        String table,
        List<String> pkNames,
        @JsonDeserialize(using = Boolean01Deserializer.class)
        Boolean isDdl,
        TiCdcEventTypeEnum type,
        Long es,
        Long ts,
        String sql,
        Map<String, Integer> sqlType,
        Map<String, String> mysqlType,
        List<T> data,
        List<T> old
) {
    public TiCdcEvent {
        if (database == null) database = "";
        if (table == null) table = "";
        if (pkNames == null) pkNames = List.of();
        if (isDdl == null) isDdl = false;
        if (sql == null) sql = "";
        if (sqlType == null) sqlType = Map.of();
        if (mysqlType == null) mysqlType = Map.of();
        if (data == null) data = List.of();
        if (old == null) old = List.of();
    }

    public boolean isInsert() {
        return TiCdcEventTypeEnum.INSERT.equals(type);
    }

    public boolean isUpdate() {
        return TiCdcEventTypeEnum.UPDATE.equals(type);
    }

    public boolean isDelete() {
        return TiCdcEventTypeEnum.DELETE.equals(type);
    }

    public T firstData() {
        return data.isEmpty() ? null : data.getFirst();
    }

    public T firstOld() {
        return old.isEmpty() ? null : old.getFirst();
    }
}