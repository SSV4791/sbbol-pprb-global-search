package ru.sberbank.pprb.sbbol.global_search.engine.bean;

/**
 * Интерфейс провайдера бинов. Для получения бинов с использованием DI-фреймворка нужно реализовать этот интерфейс
 * поставляющим бины с использованием соответствующего фреймворка.
 */
public interface BeanProvider extends AutoCloseable {

    /**
     * Получить бин соответствующего класса
     *
     * @param clazz класс бина
     * @param <T>   возвращаемый тип бина (может быть интерфейсом или родительским классом для класса бина)
     * @throws ClassCastException если в контексте есть бин с заданным именем, но неподходящим типом
     */
    <T> BeanHolder<T> getBean(Class<? extends T> clazz);

    /**
     * Получить бин соответствующего класса по его имени
     *
     * @param clazz    класс бина
     * @param beanName имя бина
     * @param <T>      возвращаемый тип бина (может быть интерфейсом или родительским классом для класса бина)
     * @throws ClassCastException если в контексте есть бин с заданным именем, но неподходящим типом
     */
    <T> BeanHolder<T> getBean(Class<? extends T> clazz, String beanName);
}
