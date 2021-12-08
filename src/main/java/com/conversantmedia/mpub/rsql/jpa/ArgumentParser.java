/*
 * The MIT License
 *
 * Copyright 2013 Jakub Jirutka <jakub@jirutka.cz>.
 * Copyright 2015 Antonio Rabelo
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package com.conversantmedia.mpub.rsql.jpa;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.core.convert.ConversionService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Cloned from rsql-hibernate project and simplified by using Spring's {@link ConversionService}
 */
@Slf4j
@RequiredArgsConstructor
public class ArgumentParser {

    private final ConversionService conversionService;

    /**
     * Parse single argument from string value
     */
    @SuppressWarnings("unchecked")
    public <T> T parse(String argument, Class<T> type) {
        log.debug("Parsing argument {} as type {}", argument, type.getSimpleName());
        if(argument.contains("*"))
            return  (T) argument;
        return conversionService.convert(argument, type);

    }

    /**
     * Parse a list of arguments
     */
    public <T> List<T> parse(@NotNull List<String> arguments, Class<T> type) {
        return arguments.stream()
                .map(arg -> parse(arg, type))
                .collect(Collectors.toList());
    }
}
