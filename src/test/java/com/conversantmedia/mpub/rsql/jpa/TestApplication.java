package com.conversantmedia.mpub.rsql.jpa;

import com.conversantmedia.mpub.rsql.jpa.converter.LocalDateConverter;
import com.conversantmedia.mpub.rsql.jpa.converter.LocalDateTimeConverter;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ConversionServiceFactoryBean;

import java.util.Set;

@SpringBootApplication
public class TestApplication {

    @Bean
    public ConversionServiceFactoryBean conversionServiceFactoryBean() {
        final ConversionServiceFactoryBean conversionServiceFactoryBean = new ConversionServiceFactoryBean();
        conversionServiceFactoryBean.setConverters(
                Set.of(
                        new LocalDateTimeConverter(),
                        new LocalDateConverter()
                )
        );
        return conversionServiceFactoryBean;
    }
}
