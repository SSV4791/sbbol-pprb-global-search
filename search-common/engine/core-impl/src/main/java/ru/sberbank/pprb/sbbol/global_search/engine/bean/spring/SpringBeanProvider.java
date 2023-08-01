package ru.sberbank.pprb.sbbol.global_search.engine.bean.spring;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanProvider;

@Slf4j
public class SpringBeanProvider implements BeanProvider, ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public <T> BeanHolder<T> getBean(Class<? extends T> clazz) {
        log.debug("Получение бина по типу '{}' (Т.к. наименование бина не задано, в качестве него будет использовано имя класса)", clazz);
        String beanName = clazz.getName();
        return getBeanInternal(clazz, beanName);
    }

    @Override
    public <T> BeanHolder<T> getBean(Class<? extends T> clazz, String beanName) {
        log.debug("Получение бина по типу '{}' и наименованию '{}'", clazz, beanName);
        return getBeanInternal(clazz, beanName);
    }

    private <T> BeanHolder<T> getBeanInternal(Class<? extends T> clazz, String beanName) {
        if (!applicationContext.containsBean(beanName)) {
            log.info("В контексте отсутствует бин с именем '{}'", beanName);
            DefaultListableBeanFactory beanFactory = (DefaultListableBeanFactory) applicationContext.getAutowireCapableBeanFactory();
            BeanDefinitionBuilder beanDefinitionBuilder = BeanDefinitionBuilder.rootBeanDefinition(clazz)
                .setAutowireMode(AbstractBeanDefinition.AUTOWIRE_BY_TYPE);
            beanFactory.registerBeanDefinition(beanName, beanDefinitionBuilder.getBeanDefinition());
            log.info("Бин с именем '{}' и типом '{}' успешно добавлен", beanName, clazz);
        }
        @SuppressWarnings("unchecked") T bean = (T) applicationContext.getBean(beanName);
        return new BeanHolder<>(bean, beanName);
    }

    @Override
    public void close() throws Exception {
        // do nothing
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
