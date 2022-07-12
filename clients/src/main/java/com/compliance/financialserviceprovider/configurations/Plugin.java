package com.compliance.financialserviceprovider.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.corda.client.jackson.JacksonSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class Plugin {
    @Bean
    public ObjectMapper registerModule() {
        return JacksonSupport.createNonRpcMapper();
    }
}