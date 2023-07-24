package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata;

import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.entity.NestedEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ValueToStringMapper;

import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Метаданные класса сущности, используемой в поисковом сервисе
 *
 * @param <T> класс сущности, используемой в поисковом сервисе
 * @see SearchableEntity
 */
public class EntityMetadataHolder<T> {

    /**
     * класс сущности
     */
    private final Class<T> entityClass;

    /**
     * наименование сущности
     */
    private final String entityName;

    /**
     * стратегия определения наименования индекса для сущности
     */
    private final IndexNameResolvingStrategy<T> indexNameResolvingStrategy;

    /**
     * маппер поля, используемого в качестве идентификатора сущности
     */
    private final ValueToStringMapper<?> entityIdMapper;

    /**
     * маппер поля, используемого в качестве значения для routing'а запросов
     */
    private final ValueToStringMapper<?> routingValueMapper;

    /**
     * дескриптор поля, используемого в качестве идентификатора сущности
     */
    private final PropertyDescriptor entityIdProperty;

    /**
     * дескриптор поля, используемого в качестве значения для routing'а запросов
     */
    private final PropertyDescriptor routingValueProperty;

    /**
     * дескриптор поля, используемого в качестве версии объекта сущности во внешней системе
     */
    private final PropertyDescriptor externalVersionProperty;

    /**
     * Класс сущности, внешней по отношению к данной (для вложенных сущностей)
     *
     * @see NestedEntity
     */
    private final Class<?> outerEntityClass;

    /**
     * Маппер поля, используемого в качестве идентификатора внешней сущности
     */
    private final ValueToStringMapper<?> outerEntityIdMapper;

    /**
     * Дескриптор поля, используемого в качестве идентификатора внешней сущности
     */
    private final PropertyDescriptor outerEntityIdProperty;

    /**
     * Поля сущности по типам
     */
    private final Map<Class<?>, List<PropertyDescriptor>> entityProperties;

    /**
     * Поля сущности, доступные для запроса в виде:
     * <p> [наименование поля класса сущности, доступное для запроса] -> [список наименований реальных полей]
     */
    private final Map<String, Collection<String>> queryableFields;

    /**
     * Поля сущности, доступные для запроса единым списком
     */
    private final Collection<String> flatQueryableFields;

    /**
     * Поля сущности, доступные для сортировки/
     * <p> [наименование поля класса сущности, доступное для запроса] -> [реальное наименование поля сущности]
     */
    private final Map<String, String> sortableField;
    /**
     * Поля-коллекции, сгруппированные по параметрам типа
     */
    private final Map<Class<?>, List<PropertyDescriptor>> entityCollectionProperties;

    private EntityMetadataHolder(Builder<T> builder) {
        entityClass = builder.entityClass;
        entityName = builder.entityName;
        indexNameResolvingStrategy = builder.indexNameResolvingStrategy;
        entityIdMapper = builder.entityIdMapper;
        routingValueMapper = builder.routingValueMapper;
        entityIdProperty = builder.entityIdProperty;
        routingValueProperty = builder.routingValueProperty;
        externalVersionProperty = builder.externalVersionProperty;
        outerEntityClass = builder.outerEntityClass;
        outerEntityIdMapper = builder.outerEntityIdMapper;
        outerEntityIdProperty = builder.outerEntityIdProperty;
        entityProperties = builder.entityProperties != null ?
            Collections.unmodifiableMap(new HashMap<>(builder.entityProperties)) : Collections.emptyMap();
        queryableFields = builder.queryableFields != null ?
            Collections.unmodifiableMap(new HashMap<>(builder.queryableFields)) : Collections.emptyMap();
        flatQueryableFields = builder.flatQueryableFields != null ?
            Collections.unmodifiableCollection(new ArrayList<>(builder.flatQueryableFields)) : Collections.emptyList();
        sortableField = builder.sortableField != null ?
            Collections.unmodifiableMap(new HashMap<>(builder.sortableField)) : Collections.emptyMap();
        entityCollectionProperties = builder.entityCollectionProperties != null ?
            Collections.unmodifiableMap(new HashMap<>(builder.entityCollectionProperties)) : Collections.emptyMap();
    }

    /**
     * Получить билдер объекта метаданных класса сущности, используемой в поисковом сервисе
     *
     * @param entityClass класс сущности
     * @param <T>         класс сущности, используемой в поисковом сервисе
     */
    public static <T> Builder<T> builder(Class<T> entityClass) {
        return new Builder<>(entityClass);
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    public IndexNameResolvingStrategy<T> getIndexNameResolvingStrategy() {
        return indexNameResolvingStrategy;
    }

    public ValueToStringMapper getEntityIdMapper() {
        return entityIdMapper;
    }


    public ValueToStringMapper getRoutingValueMapper() {
        return routingValueMapper;
    }

    public PropertyDescriptor getEntityIdProperty() {
        return entityIdProperty;
    }

    public PropertyDescriptor getRoutingValueProperty() {
        return routingValueProperty;
    }

    public PropertyDescriptor getExternalVersionProperty() {
        return externalVersionProperty;
    }

    public Map<Class<?>, List<PropertyDescriptor>> getEntityProperties() {
        return entityProperties;
    }

    public Class<?> getOuterEntityClass() {
        return outerEntityClass;
    }

    public ValueToStringMapper getOuterEntityIdMapper() {
        return outerEntityIdMapper;
    }

    public PropertyDescriptor getOuterEntityIdProperty() {
        return outerEntityIdProperty;
    }

    public Map<String, Collection<String>> getQueryableFields() {
        return queryableFields;
    }

    public Collection<String> getFlatQueryableFields() {
        return flatQueryableFields;
    }

    public Map<String, String> getSortableField() {
        return sortableField;
    }

    public Map<Class<?>, List<PropertyDescriptor>> getEntityCollectionProperties() {
        return entityCollectionProperties;
    }

    @Override
    public String toString() {
        return "EntityMetadataHolder{" +
            "entityClass=" + entityClass +
            ", entityName='" + entityName + '\'' +
            ", indexNameResolvingStrategy=" + indexNameResolvingStrategy +
            ", entityIdMapper=" + entityIdMapper +
            ", routingValueMapper=" + routingValueMapper +
            ", entityIdProperty=" + entityIdProperty +
            ", routingValueProperty=" + routingValueProperty +
            ", externalVersionProperty=" + externalVersionProperty +
            ", outerEntityClass=" + outerEntityClass +
            ", outerEntityIdMapper=" + outerEntityIdMapper +
            ", outerEntityIdProperty=" + outerEntityIdProperty +
            ", entityProperties=" + entityProperties +
            ", queryableFields=" + queryableFields +
            ", flatQueryableFields=" + flatQueryableFields +
            ", entityCollectionProperties=" + entityCollectionProperties +
            '}';
    }

    /**
     * Билдер объекта метаданных класса сущности, используемой в поисковом сервисе
     *
     * @param <T> класс сущности, используемой в поисковом сервисе
     */
    public static final class Builder<T> {

        private final Class<T> entityClass;

        private String entityName;

        private IndexNameResolvingStrategy<T> indexNameResolvingStrategy;

        private ValueToStringMapper<?> entityIdMapper;

        private ValueToStringMapper<?> routingValueMapper;

        private PropertyDescriptor entityIdProperty;

        private PropertyDescriptor routingValueProperty;

        private PropertyDescriptor externalVersionProperty;

        private Class<?> outerEntityClass;

        private ValueToStringMapper<?> outerEntityIdMapper;

        private PropertyDescriptor outerEntityIdProperty;

        private Map<Class<?>, List<PropertyDescriptor>> entityProperties;

        private Map<String, Collection<String>> queryableFields;

        private Collection<String> flatQueryableFields;

        private Map<String, String> sortableField;

        private Map<Class<?>, List<PropertyDescriptor>> entityCollectionProperties;

        public Builder(Class<T> entityClass) {
            this.entityClass = entityClass;
        }

        public Builder<T> entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder<T> indexNameResolvingStrategy(IndexNameResolvingStrategy<T> indexNameResolvingStrategy) {
            this.indexNameResolvingStrategy = indexNameResolvingStrategy;
            return this;
        }

        public Builder<T> entityIdMapper(ValueToStringMapper<?> entityIdMapper) {
            this.entityIdMapper = entityIdMapper;
            return this;
        }

        public Builder<T> routingValueMapper(ValueToStringMapper<?> routingValueMapper) {
            this.routingValueMapper = routingValueMapper;
            return this;
        }

        public Builder<T> entityIdProperty(PropertyDescriptor entityIdProperty) {
            this.entityIdProperty = entityIdProperty;
            return this;
        }

        public Builder<T> routingValueProperty(PropertyDescriptor routingValueProperty) {
            this.routingValueProperty = routingValueProperty;
            return this;
        }

        public Builder<T> externalVersionProperty(PropertyDescriptor externalVersionProperty) {
            this.externalVersionProperty = externalVersionProperty;
            return this;
        }

        public Builder<T> outerEntityClass(Class<?> outerEntityClass) {
            this.outerEntityClass = outerEntityClass;
            return this;
        }

        public Builder<T> outerEntityIdMapper(ValueToStringMapper<?> outerEntityIdMapper) {
            this.outerEntityIdMapper = outerEntityIdMapper;
            return this;
        }

        public Builder<T> outerEntityIdProperty(PropertyDescriptor outerEntityIdProperty) {
            this.outerEntityIdProperty = outerEntityIdProperty;
            return this;
        }

        public Builder<T> entityProperties(Map<Class<?>, List<PropertyDescriptor>> entityProperties) {
            this.entityProperties = entityProperties;
            return this;
        }

        public Builder<T> queryableFields(Map<String, Collection<String>> queryableFields) {
            this.queryableFields = queryableFields;
            return this;
        }

        public Builder<T> flatQueryableFields(Collection<String> flatQueryableFields) {
            this.flatQueryableFields = flatQueryableFields;
            return this;
        }

        public Builder<T> sortableFields(Map<String, String> sortableField) {
            this.sortableField = sortableField;
            return this;
        }

        public Builder<T> entityCollectionProperties(Map<Class<?>, List<PropertyDescriptor>> entityCollectionProperties) {
            this.entityCollectionProperties = entityCollectionProperties;
            return this;
        }

        public EntityMetadataHolder<T> build() {
            return new EntityMetadataHolder<>(this);
        }
    }
}
