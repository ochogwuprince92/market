package com.khane.market.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "paystack")
@Getter
@Setter
public class PaystackConfig {

    private Api api;
    private Callback callback;

    @Getter
    @Setter
    public static class Api {
        private String key;
        private String secret;
        private String url;
    }

    @Getter
    @Setter
    public static class Callback {
        private String url;
    }
}

