package com.conversantmedia.mpub.rsql.jpa.boot;

import com.conversantmedia.mpub.rsql.jpa.RSQLSpecificationFactory;
import com.conversantmedia.mpub.rsql.jpa.converter.LocalDateConverter;
import com.conversantmedia.mpub.rsql.jpa.converter.LocalDateTimeConverter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;

import javax.persistence.EntityManagerFactory;

/**
 * Spring Boot autoconfiguration for rSQL JPA
 */
@Configuration
//@ConditionalOnBean(EntityManagerFactory.class)
//@ConditionalOnMissingBean(RSQLSpecificationFactory.class)
//@ConditionalOnProperty(prefix = "rsq.jpa", name = "enabled", matchIfMissing = true)
public class RSQLJpaAutoConfiguration {

    @Bean
    public RSQLSpecificationFactory rsqlSpecificationFactory(ConversionService conversionService, EntityManagerFactory entityManagerFactory) {
        return new RSQLSpecificationFactory(conversionService, entityManagerFactory);
    }

    @Bean
    public LocalDateTimeConverter localDateTimeConverter() {
        return new LocalDateTimeConverter();
    }

    @Bean
    public LocalDateConverter localDateConverter() {
        return new LocalDateConverter();
    }
}
