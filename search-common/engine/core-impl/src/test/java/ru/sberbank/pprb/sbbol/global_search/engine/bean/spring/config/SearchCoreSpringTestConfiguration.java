package ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.SpringBeanProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.data.DummyService;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.data.DummyServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.data.DummyServiceImpl2;

@Configuration
public class SearchCoreSpringTestConfiguration {

    @Bean
    BeanProvider beanProvider() {
        return new SpringBeanProvider();
    }

    @Bean
    DummyService dummyService() {
        return new DummyServiceImpl();
    }

    @Bean(name = "dummyService2")
    DummyService dummyService2() {
        return new DummyServiceImpl2();
    }
}
