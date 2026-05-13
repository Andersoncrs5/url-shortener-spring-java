package com.write.api.shared.mapper.config;

import org.springframework.stereotype.Component;

@Component
public class MapperConfig {

    public boolean map(Byte value) {
        return value != null && value == 1;
    }

    public Byte map(boolean value) {
        return (byte) (value ? 1 : 0);
    }

}
