package com.compliance.auditor.configurations;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.corda.client.jackson.JacksonSupport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * This configuration registers a Jackson module that knows how to serialize and
 * deserialize the types that are used in the Corda RPC protocol
 */
@Configuration
class Plugin {
    @Bean
    public ObjectMapper registerModule() {
        return JacksonSupport.createNonRpcMapper();
    }
}