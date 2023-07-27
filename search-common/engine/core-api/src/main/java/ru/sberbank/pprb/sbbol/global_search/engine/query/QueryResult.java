package ru.sberbank.pprb.sbbol.global_search.engine.query;

import java.util.Collection;

/**
 * Результат запроса к сущности, используемой в поисковом сервисе
 *
 * @param <T> тип объекта сущности
 */
public class QueryResult<T> {

    /**
     * Класс объекта сущности
     */
    private final Class<T> entityClass;

    /**
     * Коллекция объектов сущности - результатов запроса
     */
    private final Collection<T> entities;

    /**
     * Продолжительность выполнения запроса
     */
    private final long duration;

    /**
     * Общее количество результатов запроса
     * <p>
     * Коллекция результатов запроса содержит только {@link EntityQuery#maxResultCount} результатов,
     * в то время как тут указывается общее количество найденных результатов
     */
    private final long totalResultCount;

    /**
     * Ошибка в ходе выполнения запроса
     */
    private final Exception searchFailure;

    private QueryResult(Builder<T> builder) {
        this.entityClass = builder.entityClass;
        this.entities = builder.entities;
        this.duration = builder.duration;
        this.totalResultCount = builder.totalResultCount;
        this.searchFailure = builder.searchFailure;
    }

    /**
     * Получить билдер результата запроса к сущности, используемой в поисковом сервисе
     *
     * @param entityClass класс запрашиваемой сущности
     * @param <T>         тип объекта запрашиваемой сущности
     */
    public static <T> Builder<T> builder(Class<T> entityClass) {
        return new Builder<>(entityClass);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public Collection<T> getEntities() {
        return entities;
    }

    public long getDuration() {
        return duration;
    }

    public long getTotalResultCount() {
        return totalResultCount;
    }

    public Exception getSearchFailure() {
        return searchFailure;
    }

    /**
     * Билдер результата запроса к сущности, используемой в поисковом сервисе
     *
     * @param <T> тип объекта запрашиваемой сущности
     */
    public static final class Builder<T> {

        private final Class<T> entityClass;
        private Collection<T> entities;
        private long duration;
        private long totalResultCount;
        private Exception searchFailure;

        public Builder(Class<T> entityClass) {
            this.entityClass = entityClass;
        }

        public Builder<T> entities(Collection<T> entities) {
            this.entities = entities;
            return this;
        }

        public Builder<T> duration(long duration) {
            this.duration = duration;
            return this;
        }

        public Builder<T> totalResultCount(long totalResultCount) {
            this.totalResultCount = totalResultCount;
            return this;
        }

        public Builder<T> searchFailure(Exception searchFailure) {
            this.searchFailure = searchFailure;
            return this;
        }

        public QueryResult<T> build() {
            return new QueryResult<>(this);
        }
    }
}
