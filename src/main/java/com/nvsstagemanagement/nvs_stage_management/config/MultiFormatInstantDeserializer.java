package com.nvsstagemanagement.nvs_stage_management.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class MultiFormatInstantDeserializer extends StdDeserializer<Instant> {

    private static final DateTimeFormatter[] FORMATTERS = new DateTimeFormatter[] {
            DateTimeFormatter.ISO_INSTANT,
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")

    };

    public MultiFormatInstantDeserializer() {
        super(Instant.class);
    }

    @Override
    public Instant deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String dateStr = p.getText().trim();
        for (DateTimeFormatter formatter : FORMATTERS) {
            try {

                if (formatter.equals(DateTimeFormatter.ISO_INSTANT)) {
                    return Instant.from(formatter.parse(dateStr));
                } else {
                    LocalDateTime ldt = LocalDateTime.parse(dateStr, formatter);
                    return ldt.toInstant(ZoneOffset.systemDefault().getRules().getOffset(ldt));
                }
            } catch (Exception e) {

            }
        }
        throw new IOException("Unable to parse date: " + dateStr);
    }
}
