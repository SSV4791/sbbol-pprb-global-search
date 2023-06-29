package ru.sberbank.pprb.sbbol.global_search.search.model;

import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.Restriction;

import java.io.Serializable;
import java.util.Collection;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

public class SearchFilter implements Serializable {

    private String query;

    private Integer size;

    private Integer offset;


    private Map<SearchableEntityType, Collection<Restriction>> entityTypeRestrictions = new EnumMap<>(SearchableEntityType.class);

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }

    public Map<SearchableEntityType, Collection<Restriction>> getEntityTypeRestrictions() {
        return entityTypeRestrictions;
    }

    public void setEntityTypeRestrictions(Map<SearchableEntityType, Collection<Restriction>> entityTypeRestrictions) {
        this.entityTypeRestrictions = entityTypeRestrictions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SearchFilter that = (SearchFilter) o;
        return Objects.equals(query, that.query) && Objects.equals(size, that.size) && Objects.equals(offset, that.offset) && Objects.equals(entityTypeRestrictions, that.entityTypeRestrictions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query, size, offset, entityTypeRestrictions);
    }

    @Override
    public String toString() {
        return "SearchFilter{" +
            "query='" + query + '\'' +
            ", size=" + size +
            ", offset=" + offset +
            ", entityTypeRestrictions=" + entityTypeRestrictions +
            '}';
    }
}
