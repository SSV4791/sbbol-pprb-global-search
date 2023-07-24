package ru.sberbank.pprb.sbbol.global_search.engine.bean;

/**
 * Обертка над объектом бина
 *
 * @param <T> тип бина
 */
public class BeanHolder<T> {

    /**
     * Бин
     */
    private final T bean;

    /**
     * Имя бина
     */
    private final String name;

    public BeanHolder(T bean, String name) {
        this.bean = bean;
        this.name = name;
    }

    public T getBean() {
        return bean;
    }

    public String getName() {
        return name;
    }
}
