package ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.EntityIdMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ExternalVersion;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategy;
import ru.sberbank.pprb.sbbol.global_search.core.entity.IndexNameResolvingStrategyRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.NestedEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.OuterEntityId;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Queryable;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValue;
import ru.sberbank.pprb.sbbol.global_search.core.entity.RoutingValueMapperRef;
import ru.sberbank.pprb.sbbol.global_search.core.entity.SearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.core.entity.Sortable;
import ru.sberbank.pprb.sbbol.global_search.core.entity.ValueToStringMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.bean.BeanProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataLoader;

import java.beans.FeatureDescriptor;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Реализация загрузчика метаданных классов сущностей, используемых в поисковом сервисе.
 * Загружает метаданные сущностей. отмеченных аннотацией {@link SearchableEntity} или {@link NestedEntity}
 */
public class EntityMetadataLoaderImpl implements EntityMetadataLoader {

    private final BeanProvider beanProvider;

    private static final Collection<Class<?>> EXTERNAL_VERSION_PROPERTY_ALLOWED_CLASSES =
        Set.of(Integer.class, int.class, Long.class, long.class, Short.class, short.class, Byte.class, byte.class);

    private static final Logger log = LoggerFactory.getLogger(EntityMetadataLoaderImpl.class);

    public EntityMetadataLoaderImpl(BeanProvider beanProvider) {
        this.beanProvider = beanProvider;
    }

    @Override
    public <T> EntityMetadataHolder<T> load(Class<T> entityClass) {
        log.info("Загрузка метаданных класса '{}'", entityClass);

        SearchableEntity searchableEntityAnnotation = entityClass.getAnnotation(SearchableEntity.class);
        NestedEntity nestedEntityAnnotation = entityClass.getAnnotation(NestedEntity.class);
        if (searchableEntityAnnotation == null && nestedEntityAnnotation == null) {
            throw new IllegalStateException("Класс " + entityClass + " не является сущностью, индексируемой в поисковом сервисе");
        }

        EntityMetadataHolder.Builder<T> builder = EntityMetadataHolder.builder(entityClass);

        Collection<BeanProperty> beanProperties;
        try {
            beanProperties = getBeanProperties(entityClass);
        } catch (IntrospectionException e) {
            throw new RuntimeException(e);
        }
        Map<Class<?>, List<PropertyDescriptor>> entityPropertiesByTypes = beanProperties.stream()
            .map(beanProperty -> beanProperty.descriptor)
            .collect(Collectors.groupingBy(PropertyDescriptor::getPropertyType));
        builder.entityProperties(entityPropertiesByTypes);

        Map<Class<?>, List<PropertyDescriptor>> entityCollectionProperties = beanProperties.stream()
            .filter(beanProperty -> Collection.class.isAssignableFrom(beanProperty.field.getType()))
            .map(beanProperty -> {
                PropertyDescriptor descriptor = beanProperty.descriptor;
                Type propertyType = descriptor.getReadMethod().getGenericReturnType();
                if (propertyType instanceof ParameterizedType) {
                    ParameterizedType parameterizedType = (ParameterizedType) propertyType;
                    Type[] fieldArgTypes = parameterizedType.getActualTypeArguments();
                    Class<?> argClass = (Class<?>) fieldArgTypes[0];
                    return Pair.of(argClass, descriptor);
                }
                return null;
            })
            .filter(Objects::nonNull)
            .collect(Collectors.groupingBy(Pair::getKey, Collectors.mapping(Pair::getValue, Collectors.toList())));
        builder.entityCollectionProperties(entityCollectionProperties);

        try {
            BeanProperty beanProperty = getAnnotatedProperty(beanProperties, EntityId.class, true);
            builder.entityIdProperty(beanProperty.descriptor);
            EntityId fieldAnnotation = beanProperty.field.getAnnotation(EntityId.class);
            EntityIdMapperRef mapperRef = fieldAnnotation.mapper();
            String beanName = mapperRef.beanName();
            Class<? extends ValueToStringMapper> beanType = mapperRef.type();
            builder.entityIdMapper(resolveBean(beanType, beanName));
        } catch (Exception e) {
            throw new IllegalStateException("При определении поля - идентификатора сущности для класса " + entityClass.getName() +
                " возникла ошибка", e);
        }

        Map<String, Collection<String>> queryableFields = getAnnotatedProperties(beanProperties, Queryable.class).stream()
            .collect(Collectors.toMap(
                beanProperty -> beanProperty.field.getName(),
                beanProperty -> {
                    Queryable fieldAnnotation = beanProperty.field.getAnnotation(Queryable.class);
                    String[] fields = fieldAnnotation.value();
                    return fields.length > 0 ? Arrays.asList(fields) : Collections.singletonList(beanProperty.field.getName());
                }
            ));
        builder.queryableFields(queryableFields)
            .flatQueryableFields(queryableFields.values().stream().flatMap(Collection::stream).collect(Collectors.toList()));

        if (searchableEntityAnnotation != null) {
            builder.entityName(searchableEntityAnnotation.name());
            IndexNameResolvingStrategyRef indexNameResolvingStrategyRef = searchableEntityAnnotation.indexNameResolvingStrategy();
            String indexNameResolvingStrategyBeanName = indexNameResolvingStrategyRef.beanName();
            Class<? extends IndexNameResolvingStrategy> indexNameResolvingStrategyBeanType = indexNameResolvingStrategyRef.type();
            @SuppressWarnings("unchecked")
            IndexNameResolvingStrategy<T> indexNameResolvingStrategyBean = resolveBean(indexNameResolvingStrategyBeanType, indexNameResolvingStrategyBeanName);
            builder.indexNameResolvingStrategy(indexNameResolvingStrategyBean);

            try {
                BeanProperty beanProperty = getAnnotatedProperty(beanProperties, RoutingValue.class, false);
                if (beanProperty != null) {
                    builder.routingValueProperty(beanProperty.descriptor);
                    RoutingValue fieldAnnotation = beanProperty.field.getAnnotation(RoutingValue.class);
                    RoutingValueMapperRef mapperRef = fieldAnnotation.mapper();
                    String beanName = mapperRef.beanName();
                    Class<? extends ValueToStringMapper> beanType = mapperRef.type();
                    builder.routingValueMapper(resolveBean(beanType, beanName));
                }
            } catch (Exception e) {
                throw new IllegalStateException("При определении поля - значения routing'а запросов для класса " + entityClass.getName() +
                    " возникла ошибка", e);
            }

            try {
                BeanProperty beanProperty = getAnnotatedProperty(beanProperties, ExternalVersion.class, false);
                if (beanProperty != null) {
                    PropertyDescriptor versionPropertyDescriptor = beanProperty.descriptor;
                    Class<?> versionPropertyType = versionPropertyDescriptor.getPropertyType();
                    if (!EXTERNAL_VERSION_PROPERTY_ALLOWED_CLASSES.contains(versionPropertyType)) {
                        throw new IllegalStateException("Недопустимый тип поля версии объекта сущности во внешней системе для класса " +
                            entityClass.getName() + ". Список допустимых типов: " + EXTERNAL_VERSION_PROPERTY_ALLOWED_CLASSES);
                    }
                    builder.externalVersionProperty(versionPropertyDescriptor);
                }
            } catch (Exception e) {
                throw new IllegalStateException("При определении поля - значения routing'а запросов для класса " + entityClass.getName() +
                    " возникла ошибка", e);
            }

            try {
                Map<String, String> sortableFields = getAnnotatedProperties(beanProperties, Sortable.class).stream()
                    .collect(Collectors.toMap(
                        beanProperty -> beanProperty.field.getName(),
                        beanProperty -> {
                            Sortable fieldAnnotation = beanProperty.field.getAnnotation(Sortable.class);
                            String field = fieldAnnotation.value();
                            return "".equals(field) ? beanProperty.field.getName() : field;
                        }
                    ));
                sortableFields.put("score", "score");
                builder.sortableFields(sortableFields);

            } catch (Exception e) {
                throw new IllegalStateException("При определении поля - значения sorting'а запросов для класса " + entityClass.getName() +
                    " возникла ошибка", e);
            }
        } else if (nestedEntityAnnotation != null) {
            Class<?> outerEntityClass = nestedEntityAnnotation.outerEntityType();
            String outerEntityClassName = nestedEntityAnnotation.outerEntityTypeName();
            if (outerEntityClass == Void.class && "".equals(outerEntityClassName)) {
                throw new IllegalStateException("Для класса вложенной сущности " + entityClass.getName() + " не указан тип внешней сущности");
            }
            if (!"".equals(outerEntityClassName)) {
                Class<?> outerEntityClassForName;
                try {
                    outerEntityClassForName = Class.forName(outerEntityClassName);
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("При определении типа внешней сущности для класса вложенной сущности " +
                        entityClass.getName() + " возникла ошибка", e);
                }
                if (outerEntityClass == Void.class) {
                    outerEntityClass = outerEntityClassForName;
                }
                if (outerEntityClass != outerEntityClassForName) {
                    throw new IllegalStateException("Для класса вложенной сущности " + entityClass.getName() +
                        " невозможно однозначно определить тип внешней сущности");
                }
            }
            builder.outerEntityClass(outerEntityClass);
            try {
                BeanProperty beanProperty = getAnnotatedProperty(beanProperties, OuterEntityId.class, true);
                builder.outerEntityIdProperty(beanProperty.descriptor);
                OuterEntityId fieldAnnotation = beanProperty.field.getAnnotation(OuterEntityId.class);
                EntityIdMapperRef mapperRef = fieldAnnotation.mapper();
                String beanName = mapperRef.beanName();
                Class<? extends ValueToStringMapper> beanType = mapperRef.type();
                builder.outerEntityIdMapper(resolveBean(beanType, beanName));
            } catch (Exception e) {
                throw new IllegalStateException("При определении поля - идентификатора сущности для класса " + entityClass.getName() +
                    " возникла ошибка", e);
            }
        }

        return builder.build();
    }

    private <T> T resolveBean(Class<? extends T> clazz, String beanName) {
        BeanHolder<T> holder = StringUtils.isNotBlank(beanName) ? beanProvider.getBean(clazz, beanName) : beanProvider.getBean(clazz);
        return holder.getBean();
    }


    private Collection<BeanProperty> getBeanProperties(Class<?> beanClass) throws IntrospectionException {
        Set<String> propertyNames = Stream.of(Introspector.getBeanInfo(beanClass).getPropertyDescriptors())
            .map(FeatureDescriptor::getName)
            .collect(Collectors.toSet());

        Collection<BeanProperty> result = new ArrayList<>();
        for (Class<?> cls = beanClass; !cls.equals(Object.class); cls = cls.getSuperclass()) {
            for (Field field : cls.getDeclaredFields()) {
                // поле может и не быть свойством бина, поэтому нужно проверять
                if (propertyNames.contains(field.getName())) {
                    result.add(new BeanProperty(field, new PropertyDescriptor(field.getName(), cls)));
                }
            }
        }
        return result;
    }

    private Collection<BeanProperty> getAnnotatedProperties(Collection<BeanProperty> allProperties, Class<? extends Annotation> annotation) {
        return allProperties.stream()
            .filter(beanProperty -> beanProperty.field.isAnnotationPresent(annotation))
            .collect(Collectors.toList());
    }

    private BeanProperty getAnnotatedProperty(Collection<BeanProperty> allProperties, Class<? extends Annotation> annotation, boolean mandatory) {
        Collection<BeanProperty> annotatedProperties = getAnnotatedProperties(allProperties, annotation);
        if (annotatedProperties.size() > 1) {
            throw new IllegalStateException("Найдено более одного поля, отмеченного @" + annotation.getSimpleName());
        } else if (annotatedProperties.isEmpty()) {
            if (mandatory) {
                throw new IllegalStateException("Поле, отмеченное @" + annotation.getSimpleName() + ", не найдено");
            } else {
                return null;
            }
        }
        return annotatedProperties.iterator().next();
    }

    /**
     * Связь между полем класса и описанием свойства бина этого класса
     */
    private static class BeanProperty {
        private final Field field;
        private final PropertyDescriptor descriptor;

        private BeanProperty(Field field, PropertyDescriptor descriptor) {
            this.field = field;
            this.descriptor = descriptor;
        }
    }
}
