package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import com.google.common.collect.ImmutableMap;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.global_search.facade.ActionResultType;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchClientFacade;
import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityService;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.SearchableEntityServiceImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.ChildTestEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.ChildTestEntityIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityIdMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityRoutingValueMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.ConditionMapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SearchableEntityServiceImplTest {

    @Test
    void saveNewEntity() throws Exception {
        EntityMetadataProvider entityMetadataProvider = getMetadataProviderMock(
            Collections.singletonMap(
                ChildTestEntity.class, generateChildTestEntityMetadata())
        );

        ChildTestEntity childEntity = generateChildEntity();
        String childEntityId = childEntity.getEntityId();
        String routingValue = childEntity.getRoutingValue();
        InternalEntityHolder<ChildTestEntity> childEntityHolder =
            InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                .routingValue(routingValue)
                .build();
        EntityToInternalEntityHolderConverter converter = getEntityToInternalEntityHolderConverterMock(
            Collections.singletonMap(
                childEntity, childEntityHolder)
        );

        ConditionMapper conditionMapper = getConditionMapperMock();
        OpenSearchClientFacade facade = getOpenSearchClientFacadeMock();
        SearchableEntityService searchableEntityService = new SearchableEntityServiceImpl(facade, converter, entityMetadataProvider, conditionMapper);

        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId, routingValue)).thenReturn(null);
        when(facade.create(childEntityHolder, false)).thenReturn(ActionResultType.VERSION_CONFLICT, ActionResultType.SUCCESS);

        searchableEntityService.save(childEntity);
    }

    @Test
    void updateEntity() throws Exception {
        EntityMetadataProvider entityMetadataProvider = getMetadataProviderMock(
            Collections.singletonMap(
                ChildTestEntity.class, generateChildTestEntityMetadata())
        );

        ChildTestEntity childEntity = generateChildEntity();
        String childEntityId = childEntity.getEntityId();
        String routingValue = childEntity.getRoutingValue();
        EntityToInternalEntityHolderConverter converter = getEntityToInternalEntityHolderConverterMock(
            Collections.singletonMap(
                childEntity, InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                    .routingValue(routingValue)
                    .build()
            ));

        ConditionMapper conditionMapper = getConditionMapperMock();
        OpenSearchClientFacade facade = getOpenSearchClientFacadeMock();
        SearchableEntityService searchableEntityService = new SearchableEntityServiceImpl(facade, converter, entityMetadataProvider, conditionMapper);

        InternalEntityHolder<ChildTestEntity> childEntityHolder =
            InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                .routingValue(childEntity.getRoutingValue())
                .seqNo(1L)
                .primaryTerm(2L)
                .build();

        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId, routingValue)).thenReturn(childEntityHolder);
        when(facade.save(any())).thenReturn(ActionResultType.SUCCESS);

        searchableEntityService.save(childEntity);
    }

    @Test
    void updateEntityVersionConflict() throws Exception {
        EntityMetadataProvider entityMetadataProvider = getMetadataProviderMock(
            Collections.singletonMap(
                ChildTestEntity.class, generateChildTestEntityMetadata())
        );

        ChildTestEntity childEntity = generateChildEntity();
        String childEntityId = childEntity.getEntityId();
        String routingValue = childEntity.getRoutingValue();

        ChildTestEntity newEntity = new ChildTestEntity();
        newEntity.setEntityId(childEntityId);
        newEntity.setRoutingValue(routingValue);
        newEntity.setVersion(childEntity.getVersion() + 1);
        newEntity.setNestedTestEntity(childEntity.getNestedTestEntity());

        EntityToInternalEntityHolderConverter converter = getEntityToInternalEntityHolderConverterMock(
            ImmutableMap.of(
                childEntity, InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                    .routingValue(routingValue)
                    .build(),
                newEntity, InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, newEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                    .routingValue(routingValue)
                    .build()
            ));

        ConditionMapper conditionMapper = getConditionMapperMock();
        OpenSearchClientFacade facade = getOpenSearchClientFacadeMock();
        SearchableEntityService searchableEntityService = new SearchableEntityServiceImpl(facade, converter, entityMetadataProvider, conditionMapper);

        InternalEntityHolder<ChildTestEntity> childEntityHolder =
            InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                .routingValue(childEntity.getRoutingValue())
                .seqNo(1L)
                .primaryTerm(2L)
                .build();

        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntityId, routingValue)).thenReturn(childEntityHolder);
        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId, routingValue)).thenReturn(childEntityHolder);
        when(facade.save(any())).thenReturn(ActionResultType.VERSION_CONFLICT, ActionResultType.SUCCESS);

        Logger logger = (Logger) LoggerFactory.getLogger(SearchableEntityServiceImpl.class);
        logger.setLevel(Level.WARN);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        searchableEntityService.save(newEntity);

        List<ILoggingEvent> logList = listAppender.list;
        assertEquals(1, logList.size());
        assertThat(logList.get(0).getFormattedMessage(), containsString("При сохранении записи возник конфликт внутренней версии"));
    }

    @Test
    void updateEntityWithNewerVersion() throws Exception {
        EntityMetadataProvider entityMetadataProvider = getMetadataProviderMock(
            Collections.singletonMap(
                ChildTestEntity.class, generateChildTestEntityMetadata())
        );

        ChildTestEntity childEntity = generateChildEntity();
        String childEntityId = childEntity.getEntityId();
        String routingValue = childEntity.getRoutingValue();

        ChildTestEntity newEntity = new ChildTestEntity();
        newEntity.setEntityId(childEntityId);
        newEntity.setRoutingValue(routingValue);
        newEntity.setVersion(childEntity.getVersion() - 1);
        newEntity.setNestedTestEntity(childEntity.getNestedTestEntity());

        EntityToInternalEntityHolderConverter converter = getEntityToInternalEntityHolderConverterMock(
            ImmutableMap.of(
                childEntity, InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                    .routingValue(routingValue)
                    .build(),
                newEntity, InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, newEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                    .routingValue(routingValue)
                    .build()
            ));

        ConditionMapper conditionMapper = getConditionMapperMock();
        OpenSearchClientFacade facade = getOpenSearchClientFacadeMock();
        SearchableEntityService searchableEntityService = new SearchableEntityServiceImpl(facade, converter, entityMetadataProvider, conditionMapper);

        InternalEntityHolder<ChildTestEntity> childEntityHolder =
            InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                .routingValue(childEntity.getRoutingValue())
                .seqNo(1L)
                .primaryTerm(2L)
                .build();

        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntityId, routingValue)).thenReturn(childEntityHolder);
        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId, routingValue)).thenReturn(childEntityHolder);
        when(facade.save(any())).thenReturn(ActionResultType.VERSION_CONFLICT, ActionResultType.SUCCESS);

        Logger logger = (Logger) LoggerFactory.getLogger(SearchableEntityServiceImpl.class);
        logger.setLevel(Level.WARN);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        searchableEntityService.save(newEntity);

        List<ILoggingEvent> logList = listAppender.list;
        assertEquals(1, logList.size());
        assertThat(logList.get(0).getFormattedMessage(), containsString("Версия сохраняемой сущности меньше либо равна версии этой сущности, хранящейся в поисковом сервисе"));
    }

    @Test
    void saveNewNestedEntity() throws Exception {
        EntityMetadataProvider entityMetadataProvider = getMetadataProviderMock(
            ImmutableMap.of(
                NestedTestEntity.class, generateNestedTestEntityMetadata(),
                ChildTestEntity.class, generateChildTestEntityMetadata())
        );

        ChildTestEntity childEntity = generateChildEntity();
        String childEntityId = childEntity.getEntityId();

        EntityToInternalEntityHolderConverter converter = getEntityToInternalEntityHolderConverterMock(
            Collections.singletonMap(
                childEntity, InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                    .routingValue(childEntity.getRoutingValue())
                    .build()
            ));

        ConditionMapper conditionMapper = getConditionMapperMock();
        OpenSearchClientFacade facade = getOpenSearchClientFacadeMock();
        SearchableEntityService searchableEntityService = new SearchableEntityServiceImpl(facade, converter, entityMetadataProvider, conditionMapper);

        InternalEntityHolder<ChildTestEntity> childEntityHolder =
            InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                .routingValue(childEntity.getRoutingValue())
                .seqNo(1L)
                .primaryTerm(2L)
                .build();

        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntityId, null)).thenReturn(childEntityHolder);
        when((facade.save(any()))).thenReturn(ActionResultType.VERSION_CONFLICT, ActionResultType.SUCCESS);

        searchableEntityService.save(generateNestedEntity(childEntityId));
    }

    @Test
    void saveNewNestedEntityNoParent() throws Exception {
        EntityMetadataProvider entityMetadataProvider = getMetadataProviderMock(
            ImmutableMap.of(
                NestedTestEntity.class, generateNestedTestEntityMetadata(),
                ChildTestEntity.class, generateChildTestEntityMetadata())
        );
        EntityToInternalEntityHolderConverter converter = getEntityToInternalEntityHolderConverterMock(Collections.emptyMap());
        ConditionMapper conditionMapper = getConditionMapperMock();
        OpenSearchClientFacade facade = getOpenSearchClientFacadeMock();
        SearchableEntityService searchableEntityService = new SearchableEntityServiceImpl(facade, converter, entityMetadataProvider, conditionMapper);

        String outerEntityId = UUID.randomUUID().toString();
        NestedTestEntity nestedEntityWithoutParent = generateNestedEntity(outerEntityId);
        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, outerEntityId, null)).thenReturn(null);

        assertThrows(IllegalStateException.class, () -> searchableEntityService.save(nestedEntityWithoutParent));
    }

    @Test
    void saveNewNestedEntityVersionConflict() throws Exception {
        EntityMetadataProvider entityMetadataProvider = getMetadataProviderMock(
            ImmutableMap.of(
                NestedTestEntity.class, generateNestedTestEntityMetadata(),
                ChildTestEntity.class, generateChildTestEntityMetadata())
        );

        ChildTestEntity childEntity = generateChildEntity();
        String childEntityId = childEntity.getEntityId();

        EntityToInternalEntityHolderConverter converter = getEntityToInternalEntityHolderConverterMock(
            Collections.singletonMap(
                childEntity, InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                    .routingValue(childEntity.getRoutingValue())
                    .build()
            ));

        ConditionMapper conditionMapper = getConditionMapperMock();
        OpenSearchClientFacade facade = getOpenSearchClientFacadeMock();
        SearchableEntityService searchableEntityService = new SearchableEntityServiceImpl(facade, converter, entityMetadataProvider, conditionMapper);

        InternalEntityHolder<ChildTestEntity> childEntityHolder =
            InternalEntityHolder.builder(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntity, ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, childEntityId)
                .routingValue(childEntity.getRoutingValue())
                .seqNo(1L)
                .primaryTerm(2L)
                .build();

        when(facade.get(ChildTestEntity.class, ChildTestEntity.ENTITY_NAME, childEntityId, null)).thenReturn(childEntityHolder);
        when((facade.save(any()))).thenReturn(ActionResultType.VERSION_CONFLICT, ActionResultType.SUCCESS);

        Logger logger = (Logger) LoggerFactory.getLogger(SearchableEntityServiceImpl.class);
        logger.setLevel(Level.WARN);
        ListAppender<ILoggingEvent> listAppender = new ListAppender<>();
        listAppender.start();
        logger.addAppender(listAppender);

        searchableEntityService.save(generateNestedEntity(childEntityId));

        List<ILoggingEvent> logList = listAppender.list;
        assertEquals(1, logList.size());
        assertThat(logList.get(0).getFormattedMessage(), containsString("При сохранении вложенной сущности возник конфликт внутренней версии сущности верхнего уровня"));
    }


    private ChildTestEntity generateChildEntity() {
        ChildTestEntity entity = new ChildTestEntity();
        entity.setEntityId(UUID.randomUUID().toString());
        entity.setRoutingValue(UUID.randomUUID().toString());
        entity.setVersion(1L);
        return entity;
    }

    private NestedTestEntity generateNestedEntity(String outerEntityId) {
        NestedTestEntity entity = new NestedTestEntity();
        entity.setEntityId(UUID.randomUUID().toString());
        entity.setOuterEntityId(outerEntityId);
        return entity;
    }

    private EntityMetadataHolder<NestedTestEntity> generateNestedTestEntityMetadata() throws IntrospectionException {
        SimpleTestEntityIdMapper entityIdMapper = new SimpleTestEntityIdMapper();
        PropertyDescriptor entityIdProperty = new PropertyDescriptor("entityId", NestedTestEntity.class);
        PropertyDescriptor outerEntityIdProperty = new PropertyDescriptor("outerEntityId", NestedTestEntity.class);
        return EntityMetadataHolder.builder(NestedTestEntity.class)
            .outerEntityClass(ChildTestEntity.class)
            .entityIdMapper(entityIdMapper)
            .outerEntityIdMapper(entityIdMapper)
            .entityIdProperty(entityIdProperty)
            .outerEntityIdProperty(outerEntityIdProperty)
            .build();
    }

    private EntityMetadataHolder<ChildTestEntity> generateChildTestEntityMetadata() throws IntrospectionException {
        PropertyDescriptor entityIdProperty = new PropertyDescriptor("entityId", ChildTestEntity.class);
        PropertyDescriptor routingValueProperty = new PropertyDescriptor("routingValue", ChildTestEntity.class);
        PropertyDescriptor versionProperty = new PropertyDescriptor("version", ChildTestEntity.class);
        PropertyDescriptor nestedTestEntityProperty = new PropertyDescriptor("nestedTestEntity", ChildTestEntity.class);
        return EntityMetadataHolder.builder(ChildTestEntity.class)
            .entityName(ChildTestEntity.ENTITY_NAME)
            .indexNameResolvingStrategy(new ChildTestEntityIndexNameResolvingStrategy())
            .entityIdMapper(new SimpleTestEntityIdMapper())
            .entityIdProperty(entityIdProperty)
            .routingValueMapper(new SimpleTestEntityRoutingValueMapper())
            .routingValueProperty(routingValueProperty)
            .externalVersionProperty(versionProperty)
            .entityProperties(
                ImmutableMap.of(
                    String.class, Arrays.asList(entityIdProperty, routingValueProperty),
                    long.class, Collections.singletonList(versionProperty),
                    NestedTestEntity.class, Collections.singletonList(nestedTestEntityProperty)
                ))
            .build();
    }

    private EntityToInternalEntityHolderConverter getEntityToInternalEntityHolderConverterMock(Map<?, InternalEntityHolder<?>> mockedData) throws InvocationTargetException, IllegalAccessException {
        EntityToInternalEntityHolderConverter converter = mock(EntityToInternalEntityHolderConverter.class);
        for (Map.Entry<?, InternalEntityHolder<?>> entry : mockedData.entrySet()) {
            when(converter.convert(entry.getKey())).thenAnswer(invocation -> entry.getValue());
        }
        return converter;
    }

    private EntityMetadataProvider getMetadataProviderMock(Map<Class<?>, EntityMetadataHolder<?>> mockedData) {
        EntityMetadataProvider provider = mock(EntityMetadataProvider.class);
        for (Map.Entry<Class<?>, EntityMetadataHolder<?>> entry : mockedData.entrySet()) {
            when(provider.getMetadata(entry.getKey())).thenAnswer(invocation -> entry.getValue());
        }
        return provider;
    }

    private OpenSearchClientFacade getOpenSearchClientFacadeMock() {
        return mock(OpenSearchClientFacade.class);
    }

    private ConditionMapper getConditionMapperMock() {
        return mock(ConditionMapper.class);
    }
}
