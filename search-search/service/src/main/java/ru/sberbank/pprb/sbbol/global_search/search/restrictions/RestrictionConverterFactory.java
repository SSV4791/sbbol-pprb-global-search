package ru.sberbank.pprb.sbbol.global_search.search.restrictions;


import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.Restriction;

import java.util.Map;

/**
 * Фабрика конверторов преобразования ограничений фильтра в условия запроса
 */
public class RestrictionConverterFactory {

    private final Map<Class<? extends Restriction>, RestrictionConverter> converters;

    public RestrictionConverterFactory(Map<Class<? extends Restriction>, RestrictionConverter> converters) {
        this.converters = converters;
    }

    public RestrictionConverter<Restriction> getConverter(Class<? extends Restriction> restrictionClass) {
        return converters.get(restrictionClass);
    }
}
