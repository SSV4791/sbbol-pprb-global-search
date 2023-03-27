package ru.sberbank.pprb.sbbol.global_search.engine.query;

import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.engine.query.searchafter.SearchAfter;
import ru.sberbank.pprb.sbbol.global_search.engine.query.sort.SortField;

import java.util.Collection;
import java.util.Objects;

/**
 * Запрос к сущности, используемой в поисковом сервисе
 *
 * @param <T> тип объектов запрашиваемой сущности
 */
public class EntityQuery<T> {

    /**
     * Класс объектов запрашиваемой сущности
     */
    private final Class<T> entityClass;

    /**
     * Строка поискового запроса
     */
    private final String queryString;

    /**
     * Максимальное количество возвращаемых результатов
     */
    private final int maxResultCount;

    /**
     * Индекс позиции с которой необходимо начать поиск
     */
    private final int startSearchFrom;

    /**
     * Коллекция условий поискового запроса
     */
    private final Collection<Condition> conditions;

    /**
     * Коллекция условий сортировки поискового запроса
     */
    private final Collection<SortField> sorting;

    /**
     * Коллекция атрибутов поиска после определённого значения
     */
    private final Collection<SearchAfter> searchAfter;

    private EntityQuery(EntityQuery.Builder<T> builder) {
        this.entityClass = builder.entityClass;
        this.queryString = builder.queryString;
        this.maxResultCount = builder.maxResultCount;
        this.startSearchFrom = builder.startSearchFrom;
        this.conditions = builder.conditions;
        this.sorting = builder.sorting;
        this.searchAfter = builder.searchAfter;
    }

    /**
     * Получить билдер запроса  к сущности, используемой в поисковом сервисе
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

    public Collection<SortField> getSorting() {
        return sorting;
    }

    public Collection<SearchAfter> getSearchAfter() {
        return searchAfter;
    }

    /**
     * Билдер запроса  к сущности, используемой в поисковом сервисе
     *
     * @param <T> тип объекта запрашиваемой сущности
     */
    public static final class Builder<T> {
        private final Class<T> entityClass;

        private String queryString;

        private int maxResultCount;

        private int startSearchFrom;

        private Collection<Condition> conditions;

        private Collection<SortField> sorting;

        private Collection<SearchAfter> searchAfter;

        public Builder(Class<T> entityClass) {
            this.entityClass = Objects.requireNonNull(entityClass);
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

        public Builder<T> sorting(Collection<SortField> sorting) {
            this.sorting = sorting;
            return this;
        }

        public Builder<T> searchAfter(Collection<SearchAfter> searchAfter) {
            this.searchAfter = searchAfter;
            return this;
        }

        public EntityQuery<T> build() {
            return new EntityQuery<>(this);
        }
    }
}
