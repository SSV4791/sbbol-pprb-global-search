package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.junit.jupiter.api.Test;
import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl.EntityToInternalEntityHolderConverterImpl;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.ChildTestEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.ChildTestEntityIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntity;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityIdMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityIndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.test.SimpleTestEntityRoutingValueMapper;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class EntityToInternalEntityHolderConverterTest {

    @Test
    void processMetadata() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        EntityMetadataHolder<SimpleTestEntity> metadataHolder = EntityMetadataHolder.builder(SimpleTestEntity.class)
            .entityName(SimpleTestEntity.ENTITY_NAME)
            .indexNameResolvingStrategy(new SimpleTestEntityIndexNameResolvingStrategy())
            .entityIdMapper(new SimpleTestEntityIdMapper())
            .entityIdProperty(new PropertyDescriptor("entityId", SimpleTestEntity.class))
            .routingValueMapper(new SimpleTestEntityRoutingValueMapper())
            .routingValueProperty(new PropertyDescriptor("routingValue", SimpleTestEntity.class))
            .externalVersionProperty(null)
            .build();

        EntityMetadataProvider provider = mock(EntityMetadataProvider.class);
        when(provider.getMetadata(SimpleTestEntity.class)).thenReturn(metadataHolder);

        EntityToInternalEntityHolderConverter converter = new EntityToInternalEntityHolderConverterImpl(provider);
        SimpleTestEntity simpleTestEntity = new SimpleTestEntity();
        simpleTestEntity.setEntityId(RandomStringUtils.random(20));
        simpleTestEntity.setRoutingValue(RandomStringUtils.random(20));
        InternalEntityHolder<SimpleTestEntity> entityHolder = converter.convert(simpleTestEntity);

        assertEquals(simpleTestEntity, entityHolder.getEntity());
        assertEquals(simpleTestEntity.getClass(), entityHolder.getEntityClass());
        assertEquals(metadataHolder.getEntityName(), entityHolder.getEntityName());
        assertEquals(SimpleTestEntityIndexNameResolvingStrategy.INDEX_NAME, entityHolder.getIndexName());
        assertEquals(simpleTestEntity.getEntityId(), entityHolder.getEntityId());
        assertEquals(simpleTestEntity.getRoutingValue(), entityHolder.getRoutingValue());
        assertNull(entityHolder.getSeqNo());
        assertNull(entityHolder.getPrimaryTerm());
    }

    @Test
    void processMetadataChildClass() throws IntrospectionException, InvocationTargetException, IllegalAccessException {
        EntityMetadataHolder<ChildTestEntity> metadataHolder = EntityMetadataHolder.builder(ChildTestEntity.class)
            .entityName(ChildTestEntity.ENTITY_NAME)
            .indexNameResolvingStrategy(new ChildTestEntityIndexNameResolvingStrategy())
            .entityIdMapper(new SimpleTestEntityIdMapper())
            .entityIdProperty(new PropertyDescriptor("entityId", ChildTestEntity.class))
            .routingValueMapper(new SimpleTestEntityRoutingValueMapper())
            .routingValueProperty(new PropertyDescriptor("routingValue", ChildTestEntity.class))
            .externalVersionProperty(new PropertyDescriptor("version", ChildTestEntity.class))
            .build();

        EntityMetadataProvider provider = mock(EntityMetadataProvider.class);
        when(provider.getMetadata(ChildTestEntity.class)).thenReturn(metadataHolder);

        EntityToInternalEntityHolderConverter converter = new EntityToInternalEntityHolderConverterImpl(provider);
        ChildTestEntity testEntity = new ChildTestEntity();
        testEntity.setEntityId(RandomStringUtils.random(20));
        testEntity.setRoutingValue(RandomStringUtils.random(20));
        testEntity.setVersion(RandomUtils.nextLong());
        InternalEntityHolder<ChildTestEntity> entityHolder = converter.convert(testEntity);

        assertEquals(testEntity, entityHolder.getEntity());
        assertEquals(testEntity.getClass(), entityHolder.getEntityClass());
        assertEquals(metadataHolder.getEntityName(), entityHolder.getEntityName());
        assertEquals(ChildTestEntityIndexNameResolvingStrategy.INDEX_NAME, entityHolder.getIndexName());
        assertEquals(testEntity.getEntityId(), entityHolder.getEntityId());
        assertEquals(testEntity.getRoutingValue(), entityHolder.getRoutingValue());
        assertNull(entityHolder.getSeqNo());
        assertNull(entityHolder.getPrimaryTerm());
    }
}
