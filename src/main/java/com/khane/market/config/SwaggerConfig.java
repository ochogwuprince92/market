package com.khane.market.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Market API")
                        .description("Just A Click Away")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Ochogwu Prince")
                                .email("ochogwuprince92@gmail.com")
                                .url("https://github.com/ochogwuprince92")));
    }
}