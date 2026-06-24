package com.read.api.domain.cdc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.read.api.domain.enums.TiCdcEventTypeEnum;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public record TiCdcEvent<T>(
        Long id,

        String database,

        String table,

        List<String> pkNames,

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
        return data == null || data.isEmpty() ? null : data.getFirst();
    }
}
