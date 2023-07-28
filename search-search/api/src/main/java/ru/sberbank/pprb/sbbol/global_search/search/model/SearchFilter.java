package ru.sberbank.pprb.sbbol.global_search.search.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.Restriction;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;

@Data
@EqualsAndHashCode
public class SearchFilter implements Serializable {

    private String query;

    private Integer size;

    private Integer offset;

    private Map<SearchableEntityType, Collection<Restriction>> entityTypeRestrictions = new EnumMap<>(SearchableEntityType.class);
}
