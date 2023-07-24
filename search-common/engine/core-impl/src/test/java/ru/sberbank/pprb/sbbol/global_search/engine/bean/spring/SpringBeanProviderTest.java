package ru.sberbank.pprb.sbbol.global_search.engine.bean.spring;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.config.SearchCoreSpringTestConfiguration;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.data.DummyService;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.data.DummyServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.spring.data.DummyServiceImpl2;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = SearchCoreSpringTestConfiguration.class)
class SpringBeanProviderTest {

    @Autowired
    private BeanProvider beanProvider;

    @Test
    void getDummyServiceBeanByClass() {
        BeanHolder<DummyService> dummyService = beanProvider.getBean(DummyServiceImpl.class);
        assertEquals(DummyServiceImpl.class, dummyService.getBean().getClass());
    }

    @Test
    void getDummyServiceBeanByName() {
        BeanHolder<DummyService> dummyService = beanProvider.getBean(DummyService.class, "dummyService2");
        assertEquals(DummyServiceImpl2.class, dummyService.getBean().getClass());
    }
}
