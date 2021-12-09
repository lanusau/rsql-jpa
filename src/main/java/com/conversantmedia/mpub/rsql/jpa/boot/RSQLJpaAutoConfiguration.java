package com.conversantmedia.mpub.rsql.jpa.boot;

import com.conversantmedia.mpub.rsql.jpa.RSQLSpecificationFactory;
import com.conversantmedia.mpub.rsql.jpa.converter.LocalDateConverter;
import com.conversantmedia.mpub.rsql.jpa.converter.LocalDateTimeConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.core.convert.converter.ConverterRegistry;

import javax.persistence.EntityManagerFactory;

/**
 * Spring Boot autoconfiguration for rSQL JPA
 */
@Configuration
@ConditionalOnMissingBean(RSQLSpecificationFactory.class)
@ConditionalOnProperty(prefix = "rsq.jpa", name = "enabled", matchIfMissing = true)
public class RSQLJpaAutoConfiguration {

    @Bean
    public RSQLSpecificationFactory rsqlSpecificationFactory(ConversionService conversionService, ConverterRegistry converterRegistry, EntityManagerFactory entityManagerFactory) {
        converterRegistry.addConverter(new LocalDateTimeConverter());
        converterRegistry.addConverter(new LocalDateConverter());
        return new RSQLSpecificationFactory(conversionService, entityManagerFactory);
    }
}
