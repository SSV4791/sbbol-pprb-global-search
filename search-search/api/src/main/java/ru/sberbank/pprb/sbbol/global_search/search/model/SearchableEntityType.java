package ru.sberbank.pprb.sbbol.global_search.search.model;

/**
 * Тип сущности, доступной для полнотекстового поиска
 */
public enum SearchableEntityType {
    /**
     * Контрагент
     */
    COUNTERPARTY(Counterparty.class),
    ;

    /**
     * Класс сущности
     */
    private final Class<?> entityClass;

    SearchableEntityType(Class<?> entityClass) {
        this.entityClass = entityClass;
    }

    public Class<?> getEntityClass() {
        return entityClass;
    }

    public static SearchableEntityType toSearchableEntityType(Class<?> entityClass) {
        for (SearchableEntityType value : SearchableEntityType.values()) {
            if (value.getEntityClass().equals(entityClass)) {
                return value;
            }
        }
        return null;
    }
}
