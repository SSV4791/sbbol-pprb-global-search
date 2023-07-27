package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

import org.hamcrest.collection.IsMapContaining;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.global_search.core.common.DefaultIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.common.UuidToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ValueToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl.EntityMetadataLoaderImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.ChildTestEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.ChildTestEntityIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityIdMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityRoutingValueMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.TestEntityWithNestedCollection;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntityForCollection;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntityWithDiffOuterEntityTypeAndName;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntityWithOuterEntityTypeAndName;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntityWithOuterEntityTypeName;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.nested.NestedTestEntityWithoutOuterEntityType;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.both;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.isIn;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityMetadataLoaderTest {

    @Test
    void loadMetadata() {
        BeanHolder<IndexNameResolvingStrategy> simpleTestEntityIndexNameResolvingStrategyBeanHolder =
            new BeanHolder<>(new SimpleTestEntityIndexNameResolvingStrategy(), SimpleTestEntityIndexNameResolvingStrategy.class.getName());

        BeanHolder<IndexNameResolvingStrategy> childTestEntityIndexNameResolvingStrategyBeanHolder =
            new BeanHolder<>(new ChildTestEntityIndexNameResolvingStrategy(), ChildTestEntityIndexNameResolvingStrategy.class.getName());

        BeanHolder<ValueToStringMapper> entityIdMapperBeanHolder =
            new BeanHolder<>(new SimpleTestEntityIdMapper(), SimpleTestEntityIdMapper.class.getName());

        BeanHolder<ValueToStringMapper> routingValueMapperBeanHolder =
            new BeanHolder<>(new SimpleTestEntityRoutingValueMapper(), SimpleTestEntityRoutingValueMapper.class.getName());

        BeanProvider beanProvider = mock(BeanProvider.class);
        when(beanProvider.<IndexNameResolvingStrategy>getBean(SimpleTestEntityIndexNameResolvingStrategy.class))
            .thenReturn(simpleTestEntityIndexNameResolvingStrategyBeanHolder);
        when(beanProvider.<IndexNameResolvingStrategy>getBean(ChildTestEntityIndexNameResolvingStrategy.class))
            .thenReturn(childTestEntityIndexNameResolvingStrategyBeanHolder);
        when(beanProvider.<ValueToStringMapper>getBean(SimpleTestEntityIdMapper.class))
            .thenReturn(entityIdMapperBeanHolder);
        when(beanProvider.<ValueToStringMapper>getBean(SimpleTestEntityRoutingValueMapper.class))
            .thenReturn(routingValueMapperBeanHolder);

        EntityMetadataLoader loader = new EntityMetadataLoaderImpl(beanProvider);

        EntityMetadataHolder<SimpleTestEntity> entityMetadataHolder = loader.load(SimpleTestEntity.class);
        assertEquals(SimpleTestEntity.class, entityMetadataHolder.getEntityClass());
        assertEquals(SimpleTestEntity.ENTITY_NAME, entityMetadataHolder.getEntityName());
        assertEquals(SimpleTestEntityIndexNameResolvingStrategy.class, entityMetadataHolder.getIndexNameResolvingStrategy().getClass());
        assertEquals(SimpleTestEntityIdMapper.class, entityMetadataHolder.getEntityIdMapper().getClass());
        assertEquals("entityId", entityMetadataHolder.getEntityIdProperty().getName());
        assertEquals(SimpleTestEntityRoutingValueMapper.class, entityMetadataHolder.getRoutingValueMapper().getClass());
        assertEquals("routingValue", entityMetadataHolder.getRoutingValueProperty().getName());
        assertNull(entityMetadataHolder.getExternalVersionProperty());
        assertEquals(1, entityMetadataHolder.getEntityProperties().size());
        assertEquals(2, entityMetadataHolder.getEntityProperties().get(String.class).size());
        assertEquals(2, entityMetadataHolder.getQueryableFields().size());
        assertThat(entityMetadataHolder.getQueryableFields(), IsMapContaining.hasEntry(equalTo("entityId"), equalTo(Collections.singletonList("entityId"))));
        assertThat(entityMetadataHolder.getQueryableFields(), IsMapContaining.hasEntry(equalTo("routingValue"), equalTo(Arrays.asList("realRoutingValue", "oneMoreValue"))));
        String[] expectedFlatQueryableFields = new String[]{"entityId", "realRoutingValue", "oneMoreValue"};
        assertThat(entityMetadataHolder.getFlatQueryableFields(),
            both(everyItem(isIn(Arrays.asList(expectedFlatQueryableFields)))).and(containsInAnyOrder(expectedFlatQueryableFields)));
        assertTrue(entityMetadataHolder.getEntityCollectionProperties().isEmpty());
        assertThat(entityMetadataHolder.getSortableField(), IsMapContaining.hasEntry(equalTo("score"), equalTo("score")));
        assertThat(entityMetadataHolder.getSortableField(), IsMapContaining.hasEntry(equalTo("entityId"), equalTo("entityId.raw")));

        EntityMetadataHolder<ChildTestEntity> childEntityMetadataHolder = loader.load(ChildTestEntity.class);
        assertEquals(ChildTestEntity.class, childEntityMetadataHolder.getEntityClass());
        assertEquals(ChildTestEntity.ENTITY_NAME, childEntityMetadataHolder.getEntityName());
        assertEquals(ChildTestEntityIndexNameResolvingStrategy.class, childEntityMetadataHolder.getIndexNameResolvingStrategy().getClass());
        assertEquals(SimpleTestEntityIdMapper.class, childEntityMetadataHolder.getEntityIdMapper().getClass());
        assertEquals("entityId", childEntityMetadataHolder.getEntityIdProperty().getName());
        assertEquals(SimpleTestEntityRoutingValueMapper.class, childEntityMetadataHolder.getRoutingValueMapper().getClass());
        assertEquals("routingValue", childEntityMetadataHolder.getRoutingValueProperty().getName());
        assertEquals("version", childEntityMetadataHolder.getExternalVersionProperty().getName());
        assertEquals(3, childEntityMetadataHolder.getEntityProperties().size());
        assertEquals(2, childEntityMetadataHolder.getEntityProperties().get(String.class).size());
        assertEquals(1, childEntityMetadataHolder.getEntityProperties().get(long.class).size());
        assertEquals(1, childEntityMetadataHolder.getEntityProperties().get(NestedTestEntity.class).size());
        assertEquals(2, childEntityMetadataHolder.getQueryableFields().size());
        assertThat(childEntityMetadataHolder.getQueryableFields(), IsMapContaining.hasEntry(equalTo("entityId"), equalTo(Collections.singletonList("entityId"))));
        assertThat(childEntityMetadataHolder.getQueryableFields(), IsMapContaining.hasEntry(equalTo("routingValue"), equalTo(Arrays.asList("realRoutingValue", "oneMoreValue"))));
        String[] expectedChildFlatQueryableFields = new String[]{"entityId", "realRoutingValue", "oneMoreValue"};
        assertThat(childEntityMetadataHolder.getFlatQueryableFields(),
            both(everyItem(isIn(Arrays.asList(expectedChildFlatQueryableFields)))).and(containsInAnyOrder(expectedChildFlatQueryableFields)));
        assertTrue(childEntityMetadataHolder.getEntityCollectionProperties().isEmpty());
    }

    @Test
    void loadNestedEntityMetadata() {
        EntityMetadataHolder<NestedTestEntity> metadataHolder = loadNestedEntityMetadataInternal(NestedTestEntity.class);

        assertEquals(NestedTestEntity.class, metadataHolder.getEntityClass());
        assertNull(metadataHolder.getEntityName());
        assertNull(metadataHolder.getIndexNameResolvingStrategy());
        assertEquals(SimpleTestEntityIdMapper.class, metadataHolder.getEntityIdMapper().getClass());
        assertEquals("entityId", metadataHolder.getEntityIdProperty().getName());
        assertEquals(SimpleTestEntityIdMapper.class, metadataHolder.getOuterEntityIdMapper().getClass());
        assertEquals("outerEntityId", metadataHolder.getOuterEntityIdProperty().getName());
        assertNull(metadataHolder.getRoutingValueMapper());
        assertNull(metadataHolder.getRoutingValueProperty());
        assertNull(metadataHolder.getExternalVersionProperty());
        assertEquals(ChildTestEntity.class, metadataHolder.getOuterEntityClass());
        assertEquals(1, metadataHolder.getEntityProperties().size());
        assertEquals(2, metadataHolder.getEntityProperties().get(String.class).size());
        assertEquals(1, metadataHolder.getQueryableFields().size());
        assertThat(metadataHolder.getQueryableFields(), IsMapContaining.hasEntry(equalTo("entityId"), equalTo(Collections.singletonList("entityId"))));
        String[] expectedFlatQueryableFields = new String[]{"entityId"};
        assertThat(metadataHolder.getFlatQueryableFields(),
            both(everyItem(isIn(Arrays.asList(expectedFlatQueryableFields)))).and(containsInAnyOrder(expectedFlatQueryableFields)));
        assertTrue(metadataHolder.getEntityCollectionProperties().isEmpty());
    }

    @Test
    void loadEntityWithNestedCollection() {
        BeanHolder<IndexNameResolvingStrategy> nameResolvingStrategyBeanHolder =
            new BeanHolder<>(new DefaultIndexNameResolvingStrategy(), DefaultIndexNameResolvingStrategy.class.getName());

        BeanHolder<ValueToStringMapper> uuidToStringMapperBeanHolder =
            new BeanHolder<>(new UuidToStringMapper(), UuidToStringMapper.class.getName());

        BeanProvider beanProvider = mock(BeanProvider.class);
        when(beanProvider.<IndexNameResolvingStrategy>getBean(DefaultIndexNameResolvingStrategy.class))
            .thenReturn(nameResolvingStrategyBeanHolder);
        when(beanProvider.<ValueToStringMapper>getBean(UuidToStringMapper.class))
            .thenReturn(uuidToStringMapperBeanHolder);

        EntityMetadataLoader loader = new EntityMetadataLoaderImpl(beanProvider);
        EntityMetadataHolder<TestEntityWithNestedCollection> metadataHolder = loader.load(TestEntityWithNestedCollection.class);

        assertEquals(TestEntityWithNestedCollection.class, metadataHolder.getEntityClass());
        assertEquals(1, metadataHolder.getEntityCollectionProperties().size());
        assertEquals(1, metadataHolder.getEntityCollectionProperties().get(NestedTestEntityForCollection.class).size());
    }

    @Test
    void loadNestedEntityWithOuterEntityTypeNameMetadata() {
        EntityMetadataHolder<NestedTestEntityWithOuterEntityTypeName> metadataHolder = loadNestedEntityMetadataInternal(NestedTestEntityWithOuterEntityTypeName.class);

        assertEquals(NestedTestEntityWithOuterEntityTypeName.class, metadataHolder.getEntityClass());
        assertNull(metadataHolder.getEntityName());
        assertNull(metadataHolder.getIndexNameResolvingStrategy());
        assertEquals(SimpleTestEntityIdMapper.class, metadataHolder.getEntityIdMapper().getClass());
        assertEquals("entityId", metadataHolder.getEntityIdProperty().getName());
        assertEquals(SimpleTestEntityIdMapper.class, metadataHolder.getOuterEntityIdMapper().getClass());
        assertEquals("outerEntityId", metadataHolder.getOuterEntityIdProperty().getName());
        assertNull(metadataHolder.getRoutingValueMapper());
        assertNull(metadataHolder.getRoutingValueProperty());
        assertNull(metadataHolder.getExternalVersionProperty());
        assertEquals(ChildTestEntity.class, metadataHolder.getOuterEntityClass());
        assertEquals(1, metadataHolder.getEntityProperties().size());
        assertEquals(2, metadataHolder.getEntityProperties().get(String.class).size());
        assertEquals(1, metadataHolder.getQueryableFields().size());
        assertThat(metadataHolder.getQueryableFields(), IsMapContaining.hasEntry(equalTo("entityId"), equalTo(Collections.singletonList("entityId"))));
        String[] expectedFlatQueryableFields = new String[]{"entityId"};
        assertThat(metadataHolder.getFlatQueryableFields(),
            both(everyItem(isIn(Arrays.asList(expectedFlatQueryableFields)))).and(containsInAnyOrder(expectedFlatQueryableFields)));
    }

    @Test
    void loadNestedEntityWithOuterEntityTypeAndNameMetadata() {
        EntityMetadataHolder<NestedTestEntityWithOuterEntityTypeAndName> metadataHolder = loadNestedEntityMetadataInternal(NestedTestEntityWithOuterEntityTypeAndName.class);

        assertEquals(NestedTestEntityWithOuterEntityTypeAndName.class, metadataHolder.getEntityClass());
        assertNull(metadataHolder.getEntityName());
        assertNull(metadataHolder.getIndexNameResolvingStrategy());
        assertEquals(SimpleTestEntityIdMapper.class, metadataHolder.getEntityIdMapper().getClass());
        assertEquals("entityId", metadataHolder.getEntityIdProperty().getName());
        assertEquals(SimpleTestEntityIdMapper.class, metadataHolder.getOuterEntityIdMapper().getClass());
        assertEquals("outerEntityId", metadataHolder.getOuterEntityIdProperty().getName());
        assertNull(metadataHolder.getRoutingValueMapper());
        assertNull(metadataHolder.getRoutingValueProperty());
        assertNull(metadataHolder.getExternalVersionProperty());
        assertEquals(ChildTestEntity.class, metadataHolder.getOuterEntityClass());
        assertEquals(1, metadataHolder.getEntityProperties().size());
        assertEquals(2, metadataHolder.getEntityProperties().get(String.class).size());
        assertEquals(1, metadataHolder.getQueryableFields().size());
        assertThat(metadataHolder.getQueryableFields(), IsMapContaining.hasEntry(equalTo("entityId"), equalTo(Collections.singletonList("entityId"))));
        String[] expectedFlatQueryableFields = new String[]{"entityId"};
        assertThat(metadataHolder.getFlatQueryableFields(),
            both(everyItem(isIn(Arrays.asList(expectedFlatQueryableFields)))).and(containsInAnyOrder(expectedFlatQueryableFields)));
    }

    @Test
    void loadNestedEntityWithoutOuterEntityTypeMetadata() {
        assertThrows(IllegalStateException.class, () ->
            loadNestedEntityMetadataInternal(NestedTestEntityWithoutOuterEntityType.class)
        );
    }

    @Test
    void loadNestedEntityWithDiffOuterEntityTypeAndNameMetadata() {
        assertThrows(IllegalStateException.class, () ->
            loadNestedEntityMetadataInternal(NestedTestEntityWithDiffOuterEntityTypeAndName.class)
        );
    }

    private <T> EntityMetadataHolder<T> loadNestedEntityMetadataInternal(Class<T> nestedEntityType) {
        BeanHolder<IndexNameResolvingStrategy> simpleTestEntityIndexNameResolvingStrategyBeanHolder =
            new BeanHolder<>(new SimpleTestEntityIndexNameResolvingStrategy(), SimpleTestEntityIndexNameResolvingStrategy.class.getName());

        BeanHolder<IndexNameResolvingStrategy> childTestEntityIndexNameResolvingStrategyBeanHolder =
            new BeanHolder<>(new ChildTestEntityIndexNameResolvingStrategy(), ChildTestEntityIndexNameResolvingStrategy.class.getName());

        BeanHolder<ValueToStringMapper> entityIdMapperBeanHolder =
            new BeanHolder<>(new SimpleTestEntityIdMapper(), SimpleTestEntityIdMapper.class.getName());

        BeanHolder<ValueToStringMapper> routingValueMapperBeanHolder =
            new BeanHolder<>(new SimpleTestEntityRoutingValueMapper(), SimpleTestEntityRoutingValueMapper.class.getName());

        BeanProvider beanProvider = mock(BeanProvider.class);
        when(beanProvider.<IndexNameResolvingStrategy>getBean(SimpleTestEntityIndexNameResolvingStrategy.class))
            .thenReturn(simpleTestEntityIndexNameResolvingStrategyBeanHolder);
        when(beanProvider.<IndexNameResolvingStrategy>getBean(ChildTestEntityIndexNameResolvingStrategy.class))
            .thenReturn(childTestEntityIndexNameResolvingStrategyBeanHolder);
        when(beanProvider.<ValueToStringMapper>getBean(SimpleTestEntityIdMapper.class))
            .thenReturn(entityIdMapperBeanHolder);
        when(beanProvider.<ValueToStringMapper>getBean(SimpleTestEntityRoutingValueMapper.class))
            .thenReturn(routingValueMapperBeanHolder);

        EntityMetadataLoader loader = new EntityMetadataLoaderImpl(beanProvider);
        return loader.load(nestedEntityType);

    }
}
