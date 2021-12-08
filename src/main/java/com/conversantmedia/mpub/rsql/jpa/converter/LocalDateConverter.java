package com.conversantmedia.mpub.rsql.jpa.converter;

import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Custom converter for {@link LocalDate}
 */
@Component
public class LocalDateConverter implements Converter<String, LocalDate> {

    @Override
    public LocalDate convert(@NotNull String source) {

        // Legacy code used to pass date time as epoch second (Long)
        try {
            final long epoch = Long.parseLong(source);
            return LocalDate.ofEpochDay(epoch);
        } catch (NumberFormatException ignored) {
        }

        // Otherwise, try to parse using ISO_LOCAL_DATE format
        try {
            return LocalDate.parse(source, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException(String.format("Cannot parse %s as LocalDateTime", source), e);
        }
    }
}
