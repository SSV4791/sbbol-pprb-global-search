package ru.sberbank.pprb.sbbol.global_search.engine.bean;

import lombok.Value;

/**
 * Обертка над объектом бина
 *
 * @param <T> тип бина
 */
@Value
public class BeanHolder<T> {

    /**
     * Бин
     */
    T bean;

    /**
     * Имя бина
     */
    String name;
}
