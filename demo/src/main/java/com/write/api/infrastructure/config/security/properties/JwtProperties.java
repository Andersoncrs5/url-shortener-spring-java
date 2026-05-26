package com.write.api.infrastructure.config.security.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter @Component
@ConfigurationProperties(prefix = "spring.security")
public class JwtProperties {

    private Exp exp = new Exp();
    private Jwt jwt = new Jwt();

    @Getter
    @Setter
    public static class Exp {
        private long token;
        private long refresh;
    }

    @Getter
    @Setter
    public static class Jwt {
        private String secret;
    }
}
