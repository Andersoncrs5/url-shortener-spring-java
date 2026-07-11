package com.notify.notify.globals.classes.cdc;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
    public TiCdcEvent {
        database = (database == null) ? "" : database;
        table = (table == null) ? "" : table;
        pkNames = (pkNames == null) ? List.of() : pkNames;
        isDdl = isDdl != null && isDdl;
        sql = (sql == null) ? "" : sql;
        sqlType = (sqlType == null) ? Map.of() : sqlType;
        mysqlType = (mysqlType == null) ? Map.of() : mysqlType;
        data = (data == null) ? List.of() : data;
        old = (old == null) ? List.of() : old;
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