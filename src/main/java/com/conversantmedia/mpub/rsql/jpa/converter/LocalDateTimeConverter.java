package com.conversantmedia.mpub.rsql.jpa.converter;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Custom converter for {@link LocalDateTime}
 */
public class LocalDateTimeConverter implements Converter<String, LocalDateTime> {

    @Override
    public LocalDateTime convert(@NotNull String source) {

        // Legacy code used to pass date time as epoch second (Long)
        try {
            final long epoch = Long.parseLong(source);
            return LocalDateTime.ofEpochSecond(epoch, 0, ZoneOffset.UTC);
        } catch (NumberFormatException ignored) {
        }

        // Otherwise, try to parse using ISO_LOCAL_DATE_TIME format
        try {
            return LocalDateTime.parse(source, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(String.format("Cannot parse %s as LocalDateTime", source), e);
        }
    }
}
