package com.server.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfig {

    @Value("${spring.data.web.pageable.one-indexed-parameters:false}")
    private boolean oneIndexedParameters;

    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableCustomizer() {
        return resolver -> resolver.setOneIndexedParameters(oneIndexedParameters);
    }
}
