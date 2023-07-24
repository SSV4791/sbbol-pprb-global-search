package ru.sberbank.pprb.sbbol.global_search.facade.search;

import ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.facade.query.sort.QuerySorting;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Внутреннее представление объекта запроса к сущности, используемой в поисковом сервисе
 *
 * @param <T> тип объекта сущности
 */
public class InternalEntitySearchQuery<T> {

    /**
     * Класс объектов запрашиваемой сущности
     */
    private final Class<T> entityClass;

    /**
     * Наименование сущности
     */
    private final String entityName;

    /**
     * Коллекция значений routing'а запросов
     */
    private final Collection<String> routingValues;

    /**
     * Строка запроса
     */
    private final String queryString;

    /**
     * Коллекция условий поискового запроса
     */
    private final Collection<Condition> conditions;

    /**
     * Коллекция условий сортировки поискового запроса
     */
    private final Collection<QuerySorting> querySorting;

    /**
     * Коллекция наименований полей сущности, доступных для поиска по ним
     */
    private final Collection<String> queryableFields;

    /**
     * Коллекция значений после которых будет выполняться поиск
     */
    private final Collection<Object> searchAfterValues;

    /**
     * Максимальное количество результатов, возвращаемых запросом
     */
    private final int maxResultCount;

    /**
     * Индекс позиции с которой необходимо начать поиск
     */
    private final int startSearchFrom;

    private InternalEntitySearchQuery(Builder<T> builder) {
        entityClass = builder.entityClass;
        entityName = builder.entityName;
        queryString = builder.queryString;
        maxResultCount = builder.maxResultCount;
        startSearchFrom = builder.startSearchFrom;
        routingValues = builder.routingValues != null ?
            Collections.unmodifiableCollection(new ArrayList<>(builder.routingValues)) : Collections.emptyList();
        conditions = builder.conditions != null ?
            Collections.unmodifiableCollection(new ArrayList<>(builder.conditions)) : Collections.emptyList();
        querySorting = builder.querySorting != null ?
            Collections.unmodifiableCollection(new ArrayList<>(builder.querySorting)) : Collections.emptyList();
        queryableFields = builder.queryableFields != null ?
            Collections.unmodifiableCollection(new ArrayList<>(builder.queryableFields)) : Collections.emptyList();
        searchAfterValues = builder.searchAfterValues != null ?
            Collections.unmodifiableCollection(new ArrayList<>(builder.searchAfterValues)) : Collections.emptyList();
    }

    /**
     * Получить билдер запроса к сущности, используемой в поисковом сервисе
     *
     * @param entityClass класс запрашиваемой сущности
     * @param entityName  наименование запрашиваемой сущности
     * @param <T>         тип объекта сущности
     */
    public static <T> Builder<T> builder(Class<T> entityClass, String entityName) {
        return new Builder<>(entityClass, entityName);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    public Collection<String> getRoutingValues() {
        return routingValues;
    }

    public String getQueryString() {
        return queryString;
    }

    public int getMaxResultCount() {
        return maxResultCount;
    }

    public int getStartSearchFrom() {
        return startSearchFrom;
    }

    public Collection<Condition> getConditions() {
        return conditions;
    }

    public Collection<QuerySorting> getQuerySorting() {
        return querySorting;
    }

    public Collection<String> getQueryableFields() {
        return queryableFields;
    }

    public Collection<Object> getSearchAfterValues() { return searchAfterValues; }

    @Override
    public String toString() {
        return "InternalEntitySearchQuery{" +
            "entityClass=" + entityClass +
            ", entityName='" + entityName + '\'' +
            ", routingValues=" + routingValues +
            ", queryString='" + queryString + '\'' +
            ", conditions=" + conditions +
            ", sorting=" + querySorting +
            ", queryableFields=" + queryableFields +
            ", searchAfterValues=" + searchAfterValues +
            ", maxResultCount=" + maxResultCount +
            ", startSearchFrom=" + startSearchFrom +
            '}';
    }

    /**
     * Билдер запроса к сущности, используемой в поисковом сервисе
     *
     * @param <T> тип объекта сущности
     */
    public static final class Builder<T> {

        private final Class<T> entityClass;

        private final String entityName;

        private Collection<String> routingValues;

        private String queryString;

        private int maxResultCount;

        private int startSearchFrom;

        private Collection<Condition> conditions;

        private Collection<QuerySorting> querySorting;

        private Collection<String> queryableFields;

        private Collection<Object> searchAfterValues;

        public Builder(Class<T> entityClass, String entityName) {
            this.entityClass = Objects.requireNonNull(entityClass);
            this.entityName = Objects.requireNonNull(entityName);
        }

        public Builder<T> routingValues(Collection<String> routingValues) {
            this.routingValues = routingValues;
            return this;
        }

        public Builder<T> queryString(String queryString) {
            this.queryString = queryString;
            return this;
        }

        public Builder<T> maxResultCount(int maxResultCount) {
            if (maxResultCount < 1) {
                throw new IllegalArgumentException("Максимальное количество результатов, возвращаемых запросом, не может быть меньше 1");
            }
            this.maxResultCount = maxResultCount;
            return this;
        }

        public Builder<T> startSearchFrom(int startSearchFrom) {
            if (startSearchFrom < 0) {
                throw new IllegalArgumentException("Индекс позиции с которой необходимо начать поиск не может быть отрицательным");
            }
            this.startSearchFrom = startSearchFrom;
            return this;
        }

        public Builder<T> conditions(Collection<Condition> conditions) {
            this.conditions = conditions;
            return this;
        }

        public Builder<T> sorting(Collection<QuerySorting> querySorting) {
            this.querySorting = querySorting;
            return this;
        }

        public Builder<T> queryableFields(Collection<String> queryableFields) {
            this.queryableFields = queryableFields;
            return this;
        }

        public Builder<T> searchAfterValues(Collection<Object> searchAfterValues) {
            this.searchAfterValues = searchAfterValues;
            return this;
        }

        public InternalEntitySearchQuery<T> build() {
            return new InternalEntitySearchQuery<>(this);
        }
    }
}
