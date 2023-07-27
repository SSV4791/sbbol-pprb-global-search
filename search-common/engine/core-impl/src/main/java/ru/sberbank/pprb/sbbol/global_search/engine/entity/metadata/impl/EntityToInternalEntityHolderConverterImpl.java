package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl;

import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityToInternalEntityHolderConverter;
import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;

import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

/**
 * Реализация конвертера объекта сущности, используемой в поиске, в объект внутреннего представления сущности
 */
public class EntityToInternalEntityHolderConverterImpl implements EntityToInternalEntityHolderConverter {

    private final EntityMetadataProvider metadataProvider;

    public EntityToInternalEntityHolderConverterImpl(EntityMetadataProvider metadataProvider) {
        this.metadataProvider = metadataProvider;
    }

    @Override
    public <T> InternalEntityHolder<T> convert(T entity) throws InvocationTargetException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) entity.getClass();
        EntityMetadataHolder<T> metadata = metadataProvider.getMetadata(entityClass);
        String indexName = metadata.getIndexNameResolvingStrategy().getIndexName(entity);
        String entityName = metadata.getEntityName();
        PropertyDescriptor entityIdPropertyDescriptor = metadata.getEntityIdProperty();
        Object value = entityIdPropertyDescriptor.getReadMethod().invoke(entity);
        @SuppressWarnings("unchecked")
        String entityId = metadata.getEntityIdMapper().map(value);
        InternalEntityHolder.Builder<T> builder = InternalEntityHolder.builder(entityClass, entityName, entity, indexName, entityId)
            .outerEntityClass(metadata.getOuterEntityClass());
        PropertyDescriptor routingValuePropertyDescriptor = metadata.getRoutingValueProperty();
        if (routingValuePropertyDescriptor != null) {
            value = routingValuePropertyDescriptor.getReadMethod().invoke(entity);
            @SuppressWarnings("unchecked")
            String routingValue = metadata.getRoutingValueMapper().map(value);
            builder.routingValue(routingValue);
        }
        PropertyDescriptor outerEntityIdPropertyDescriptor = metadata.getOuterEntityIdProperty();
        if (outerEntityIdPropertyDescriptor != null) {
            value = outerEntityIdPropertyDescriptor.getReadMethod().invoke(entity);
            @SuppressWarnings("unchecked")
            String outerEntityId = metadata.getOuterEntityIdMapper().map(value);
            builder.outerEntityId(outerEntityId);
        }
        return builder.build();
    }
}
