package ru.sberbank.pprb.sbbol.global_search.facade.search;

import lombok.Value;
import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

/**
 * Внутреннее представление объекта результата запроса к сущности, используемой в поисковом сервисе
 *
 * @param <T> тип объекта сущности
 */
@Value
public class InternalEntitySearchResult<T> {

    /**
     * Класс объекта сущности
     */
    Class<T> entityClass;

    /**
     * Коллекция объектов сущности - результатов запроса
     */
    Collection<InternalEntityHolder<T>> entities;

    /**
     * Продолжительность выполнения запроса
     */
    long duration;

    /**
     * Общее количество результатов запроса
     * <p>
     * Коллекция результатов запроса содержит только {@link InternalEntitySearchQuery#maxResultCount} результатов,
     * в то время как тут указывается общее количество найденных результатов
     */
    long totalResultCount;

    /**
     * Ошибка в ходе выполнения запроса
     */
    Exception searchFailure;

    private InternalEntitySearchResult(Builder<T> builder) {
        entityClass = builder.entityClass;
        entities = builder.entities != null ?
            Collections.unmodifiableCollection(new ArrayList<>(builder.entities)) : Collections.emptyList();
        duration = builder.duration;
        totalResultCount = builder.totalResultCount;
        searchFailure = builder.searchFailure;
    }

    /**
     * Получить пустой результат запроса к сущности, используемой в поисковом сервисе
     *
     * @param entityClass класс запрашиваемой сущности
     * @param <T>         тип объекта сущности
     */
    public static <T> InternalEntitySearchResult<T> empty(Class<T> entityClass) {
        return builder(entityClass)
            .entities(Collections.emptyList())
            .build();
    }

    /**
     * Получить результат запроса к сущности, используемой в поисковом сервисе, завершившийся ошибкой
     *
     * @param entityClass класс запрашиваемой сущности
     * @param failure     ошибка в ходе выполнения запроса
     * @param <T>         тип объекта сущности
     */
    public static <T> InternalEntitySearchResult<T> failure(Class<T> entityClass, Exception failure) {
        return builder(entityClass)
            .searchFailure(failure)
            .build();
    }

    /**
     * Получить билдер результата запроса к сущности, используемой в поисковом сервисе
     *
     * @param entityClass класс запрашиваемой сущности
     * @param <T>         тип объекта сущности
     */
    public static <T> Builder<T> builder(Class<T> entityClass) {
        return new Builder<>(entityClass);
    }

    /**
     * Билдер результата запроса к сущности, используемой в поисковом сервисе
     *
     * @param <T> тип объекта запрашиваемой сущности
     */
    public static final class Builder<T> {
        private final Class<T> entityClass;

        private Collection<InternalEntityHolder<T>> entities;

        private long duration;

        private long totalResultCount;

        private Exception searchFailure;

        public Builder(Class<T> entityClass) {
            this.entityClass = Objects.requireNonNull(entityClass);
        }


        public Builder<T> entities(Collection<InternalEntityHolder<T>> entities) {
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

        public InternalEntitySearchResult<T> build() {
            return new InternalEntitySearchResult<>(this);
        }
    }
}
