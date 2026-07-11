package com.notify.notify.configs.jackson.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import java.io.IOException;

public class Boolean01Deserializer extends JsonDeserializer<Boolean> {

    @Override
    public Boolean deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        if (p.getCurrentToken() == JsonToken.VALUE_NULL) {
            return Boolean.FALSE;
        }

        String v = p.getValueAsString();
        return "1".equals(v) || "true".equalsIgnoreCase(v);
    }

    @Override
    public Boolean getNullValue(DeserializationContext ctxt) {
        return Boolean.FALSE;
    }
}