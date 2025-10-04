package com.expensetracker.app.config.jackson;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.google.cloud.Timestamp;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class TimestampDeserializer extends JsonDeserializer<Timestamp> {
    @Override
    public Timestamp deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String text = p.getText();
        if (text == null || text.isBlank()) return null;
        // Try ISO first
        try {
            Instant instant = Instant.parse(text);
            return Timestamp.of(java.util.Date.from(instant));
        } catch (DateTimeParseException ignored) {}
        // Try MM/dd/yyyy
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM/dd/yyyy");
            LocalDate ld = LocalDate.parse(text, fmt);
            return Timestamp.of(java.util.Date.from(ld.atStartOfDay(ZoneOffset.UTC).toInstant()));
        } catch (DateTimeParseException ex) {
            throw new IOException("Invalid date format. Use ISO 8601 or MM/dd/yyyy", ex);
        }
    }
}
