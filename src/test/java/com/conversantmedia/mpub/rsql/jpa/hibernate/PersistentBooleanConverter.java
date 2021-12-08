package com.conversantmedia.mpub.rsql.jpa.hibernate;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Since Postgres has its own boolean data type but we store boolean values as integers, we need to use converter
 */
@Converter(autoApply = true)
public class PersistentBooleanConverter implements AttributeConverter<Boolean, Integer> {

    @Override
    public Integer convertToDatabaseColumn(Boolean attribute) {
        if (attribute == null) return null;

        return attribute ? 1 : 0;
    }

    @Override
    public Boolean convertToEntityAttribute(Integer dbData) {
        if (dbData == null) return null;

        return dbData == 1;
    }
}
