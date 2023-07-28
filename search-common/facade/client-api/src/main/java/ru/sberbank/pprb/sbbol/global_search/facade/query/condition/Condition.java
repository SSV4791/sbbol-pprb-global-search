package ru.sberbank.pprb.sbbol.global_search.facade.query.condition;

import org.opensearch.index.query.QueryBuilder;

import java.util.Collection;

/**
 * Условие запроса к OpenSearch
 */
public interface Condition {

    /**
     * Получить builder запроса в OpenSearch по текущему условию
     */
    QueryBuilder toQueryBuilder();

    /**
     * Возвращает признак использования условия запроса в контексте фильтра (без необходимости определения степени релевантности результата)
     */
    boolean useFilterContext();

    /**
     * Получить условие отрицания
     *
     * @param nestedCondition отрицаемое условие
     */
    static NotCondition not(Condition nestedCondition) {
        return new NotCondition(nestedCondition);
    }

    /**
     * Получить условие соответствия заданному значению идентификатора
     *
     * @param id значение идентификатора, удовлетворяющее условию
     */
    static IdCondition id(String id) {
        return new IdCondition(id);
    }

    /**
     * Получить условие соответствия списку идентификаторов
     *
     * @param ids значения идентификатора, удовлетворяющие условию
     */
    static IdsCondition ids(Collection<String> ids) {
        return new IdsCondition(ids);
    }

    /**
     * Получить условие соответствия заданному значению поля
     *
     * @param fieldName наименование поля, на которое накладывается условие
     * @param value     значение поля, удовлетворяющее условию
     * @param <T>       тип значения поля
     */
    static <T> EqualCondition<T> equals(String fieldName, T value) {
        return new EqualCondition<>(fieldName, value);
    }

    /**
     * Получить условие IN
     *
     * @param fieldName наименование поля, на которое накладывается условие
     * @param values    значения поля, удовлетворяющие условию
     * @param <T>       тип значений условия
     */
    static <T> InCondition<T> in(String fieldName, Collection<? extends T> values) {
        return new InCondition<>(fieldName, values);
    }

    /**
     * Получить условие наличия заданного поля у сущности
     *
     * @param fieldName наименование поля
     */
    static FieldExistsCondition fieldExists(String fieldName) {
        return new FieldExistsCondition(fieldName);
    }

    /**
     * Получить условие: значение поля больше заданного
     *
     * @param fieldName наименование поля, на которое накладывается условие
     * @param from      нижняя граница, не включая, выборки
     * @param <T>       тип значения поля
     */
    static <T> GtCondition<T> gt(String fieldName, T from) {
        return new GtCondition<>(fieldName, from);
    }

    /**
     * Получить условие: значение поля больше либо равно заданному
     *
     * @param fieldName наименование поля, на которое накладывается условие
     * @param from      нижняя граница, включительно, выборки
     * @param <T>       тип значения поля
     */
    static <T> GteCondition<T> gte(String fieldName, T from) {
        return new GteCondition<>(fieldName, from);
    }

    /**
     * Получить условие: значение поля меньше заданного
     *
     * @param fieldName наименование поля, на которое накладывается условие
     * @param to        верхняя граница, не включая, выборки
     * @param <T>       тип значения поля
     */
    static <T> LtCondition<T> lt(String fieldName, T to) {
        return new LtCondition<>(fieldName, to);
    }

    /**
     * Получить условие: значение поля меньше либо равно заданному
     *
     * @param fieldName наименование поля, на которое накладывается условие
     * @param to        верхняя граница, включительно, выборки
     * @param <T>       тип значения поля
     */
    static <T> LteCondition<T> lte(String fieldName, T to) {
        return new LteCondition<>(fieldName, to);
    }

    /**
     * Получить условие соответствия всем переданным условиям
     *
     * @param nestedConditions условия соответствия
     */
    static AndCondition and(Collection<Condition> nestedConditions) {
        return new AndCondition(nestedConditions);
    }

    /**
     * Получить условие соответствия хотя бы одному из переданных условий
     *
     * @param nestedConditions условия соответствия
     */
    static OrCondition or(Collection<Condition> nestedConditions) {
        return new OrCondition(nestedConditions);
    }
}
