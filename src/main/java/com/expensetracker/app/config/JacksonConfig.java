package com.expensetracker.app.config;

import com.expensetracker.app.config.jackson.TimestampDeserializer;
import com.expensetracker.app.config.jackson.TimestampSerializer;
import com.fasterxml.jackson.databind.Module;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.cloud.Timestamp;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JacksonConfig {
    @Bean
    public Module firestoreTimestampModule() {
        SimpleModule module = new SimpleModule();
        module.addSerializer(Timestamp.class, new TimestampSerializer());
        module.addDeserializer(Timestamp.class, new TimestampDeserializer());
        return module;
    }
}
