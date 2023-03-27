package ru.sberbank.pprb.sbbol.global_search.engine.index;

import java.io.IOException;
import java.util.Collection;
import java.util.function.Predicate;

/**
 * Сервис управления жизненным циклом индекса
 */
public interface IndexLifecycleService {

    /**
     * Удалить устаревшие индексы для сущности
     *
     * @param entityClass           класс сущности
     * @param obsolescencePredicate предикат, определяющий, что индекс устарел, по его наименованию
     * @param <T>                   тип класса сущности
     * @return коллекция наименований удаленных индексов
     */
    <T> Collection<String> removeObsolete(Class<T> entityClass, Predicate<String> obsolescencePredicate) throws IOException;
}
