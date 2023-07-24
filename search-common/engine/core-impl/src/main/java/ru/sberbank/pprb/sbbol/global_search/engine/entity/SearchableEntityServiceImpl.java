package ru.sberbank.pprb.sbbol.global_search.engine.entity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataProvider;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityToInternalEntityHolderConverter;
import ru.sberbank.pprb.sbbol.global_search.engine.query.EntityQuery;
import ru.sberbank.pprb.sbbol.global_search.engine.query.QueryResult;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.Condition;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.ConditionMapper;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.EqualCondition;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.InCondition;
import ru.sberbank.pprb.sbbol.global_search.engine.query.searchafter.SearchAfter;
import ru.sberbank.pprb.sbbol.global_search.engine.query.sort.SortField;
import ru.sberbank.pprb.sbbol.global_search.facade.ActionResultType;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchClientFacade;
import ru.sberbank.pprb.sbbol.global_search.facade.entity.InternalEntityHolder;
import ru.sberbank.pprb.sbbol.global_search.facade.query.sort.FieldQuerySorting;
import ru.sberbank.pprb.sbbol.global_search.facade.query.sort.QuerySorting;
import ru.sberbank.pprb.sbbol.global_search.facade.query.sort.ScoreQuerySorting;
import ru.sberbank.pprb.sbbol.global_search.facade.query.sort.SortOrder;
import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchQuery;
import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchResult;

import java.beans.PropertyDescriptor;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Реализация сервиса взаимодействия с поисковым хранилищем
 */
public class SearchableEntityServiceImpl implements SearchableEntityService {

    private final OpenSearchClientFacade facade;

    private final EntityToInternalEntityHolderConverter entityHolderConverter;

    private final EntityMetadataProvider metadataProvider;

    private final ConditionMapper conditionMapper;

    private static final Logger log = LoggerFactory.getLogger(SearchableEntityServiceImpl.class);

    public SearchableEntityServiceImpl(OpenSearchClientFacade facade, EntityToInternalEntityHolderConverter entityHolderConverter,
                                       EntityMetadataProvider metadataProvider, ConditionMapper conditionMapper) {
        this.facade = facade;
        this.entityHolderConverter = entityHolderConverter;
        this.metadataProvider = metadataProvider;
        this.conditionMapper = conditionMapper;
    }

    @Override
    public <T> void save(T entity) throws Exception {
        Class<?> entityClass = entity.getClass();
        log.debug("Сохранение сущности: {} с типом {}", entity, entityClass);
        EntityMetadataHolder<?> holder = metadataProvider.getMetadata(entityClass);
        if (holder.getOuterEntityClass() == null) {
            saveEntity(entity);
        } else {
            saveNestedEntity(entity);
        }
    }

    @Override
    public <T> void create(T entity, boolean withRefresh) throws IOException, InvocationTargetException, IllegalAccessException {
        log.debug("Добавление сущности: {}", entity);
        @SuppressWarnings("unchecked")
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata((Class<T>) entity.getClass());
        if (entityMetadata.getOuterEntityClass() != null) {
            throw new UnsupportedOperationException("Добавление вложенной сущности не поддерживается [Класс сущности - '" + entity.getClass().getName() + "']");
        }
        InternalEntityHolder<T> holder = entityHolderConverter.convert(entity);
        ActionResultType resultType = facade.create(holder, withRefresh);
        if (resultType == ActionResultType.VERSION_CONFLICT) {
            log.warn("При добавлении записи возникла ошибка. Запись с указанным EntityId уже существует [EntityName = {}, EntityId = {}]." +
                " Добавление отменено", holder.getEntityName(), holder.getEntityId());
        }
    }

    @Override
    public <T> void create(T entity) throws IOException, InvocationTargetException, IllegalAccessException {
        create(entity, false);
    }

    @Override
    public <T> T get(Class<T> clazz, String entityId, String routingValue) throws IOException {
        log.debug("Получение сущности [class = {}, id = {}, routingValue = {}]", clazz, entityId, routingValue);
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata(clazz);
        if (entityMetadata.getOuterEntityClass() != null) {
            throw new UnsupportedOperationException("Получение вложенной сущности по ИД не поддерживается [Класс сущности - '" + clazz.getName() + "']");
        }
        String entityName = entityMetadata.getEntityName();
        InternalEntityHolder<T> holder = facade.get(clazz, entityName, entityId, routingValue);
        return holder != null ? holder.getEntity() : null;
    }

    @Override
    public <T> void delete(T entity) throws IOException, InvocationTargetException, IllegalAccessException {
        log.debug("Удаление сущности: {}", entity);
        @SuppressWarnings("unchecked")
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata((Class<T>) entity.getClass());
        if (entityMetadata.getOuterEntityClass() != null) {
            throw new UnsupportedOperationException("Удаление вложенной сущности не поддерживается [Класс сущности - '" + entity.getClass().getName() + "']");
        }
        InternalEntityHolder<T> holder = entityHolderConverter.convert(entity);
        facade.delete(holder);
    }

    @Override
    public Collection<QueryResult<?>> find(Collection<EntityQuery<?>> queries) throws IOException {
        log.debug("Запрос на поиск сущностей: {}", queries);
        for (EntityQuery<?> query : queries) {
            EntityMetadataHolder<?> entityMetadata = metadataProvider.getMetadata(query.getEntityClass());
            if (entityMetadata.getOuterEntityClass() != null) {
                throw new UnsupportedOperationException("Поиск по вложенным сущностям не поддерживается [Класс сущности - '" + query.getEntityClass().getName() + "']");
            }
        }
        List<InternalEntitySearchQuery<?>> entitySearchQueries = queries.stream().map(this::map).collect(Collectors.toList());
        log.trace("Преобразованный к внутреннему представлению запрос: {}", entitySearchQueries);
        Collection<InternalEntitySearchResult<?>> searchResults = facade.findAll(entitySearchQueries);
        return searchResults.stream().map(this::map).collect(Collectors.toList());
    }

    @Override
    public <T> QueryResult<T> find(EntityQuery<T> query) {
        log.debug("Запрос на поиск сущностей: {}", query);
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata(query.getEntityClass());
        if (entityMetadata.getOuterEntityClass() != null) {
            throw new UnsupportedOperationException("Поиск по вложенным сущностям не поддерживается [Класс сущности - '" + query.getEntityClass().getName() + "']");
        }
        InternalEntitySearchQuery<T> internalEntitySearchQuery = map(query);
        log.trace("Преобразованный к внутреннему представлению запрос: {}", internalEntitySearchQuery);
        InternalEntitySearchResult<T> searchResult = facade.find(internalEntitySearchQuery);
        return map(searchResult);
    }

    private <T> void saveEntity(T entity) throws InvocationTargetException, IllegalAccessException, IOException {
        InternalEntityHolder<T> holder = entityHolderConverter.convert(entity);
        @SuppressWarnings("unchecked")
        Class<T> entityClass = (Class<T>) entity.getClass();
        String entityName = holder.getEntityName();
        String indexName = holder.getIndexName();
        String entityId = holder.getEntityId();
        String routingValue = holder.getRoutingValue();
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata(entityClass);

        InternalEntityHolder<T> storedEntityHolder = facade.get(entityClass, entityName, entityId, routingValue);
        PropertyDescriptor externalVersionProperty = entityMetadata.getExternalVersionProperty();
        boolean hasEntityMoved = storedEntityHolder != null && !indexName.equals(storedEntityHolder.getIndexName());
        if (externalVersionProperty == null) {
            ActionResultType resultType = facade.save(holder);
            if (hasEntityMoved && resultType == ActionResultType.SUCCESS) {
                facade.delete(storedEntityHolder);
            }
        } else {
            Number entityExternalVersion = (Number) externalVersionProperty.getReadMethod().invoke(entity);
            if (entityExternalVersion == null) {
                throw new IllegalArgumentException("Не указано обязательное для сущности " + entityName + " значение версии объекта");
            }
            ActionResultType resultType;
            do {
                InternalEntityHolder<T> storedEntityHolderOnIndex = facade.get(entityClass, entityName, indexName, entityId, routingValue);
                if (storedEntityHolderOnIndex == null) {
                    resultType = facade.create(holder, false);
                    if (hasEntityMoved && resultType == ActionResultType.SUCCESS) {
                        facade.delete(storedEntityHolder);
                    }
                } else {
                    T storedEntity = storedEntityHolderOnIndex.getEntity();
                    Number storedEntityExternalVersion = (Number) externalVersionProperty.getReadMethod().invoke(storedEntity);
                    if (entityExternalVersion.longValue() > storedEntityExternalVersion.longValue()) {
                        resultType = facade.save(
                            InternalEntityHolder.builder(holder)
                                .seqNo(storedEntityHolderOnIndex.getSeqNo())
                                .primaryTerm(storedEntityHolderOnIndex.getPrimaryTerm())
                                .build());
                        if (resultType == ActionResultType.VERSION_CONFLICT) {
                            log.warn("При сохранении записи возник конфликт внутренней версии [EntityName = {}, EntityId = {}]." +
                                " Попытка сохранения будет произведена повторно", entityName, entityId);
                        }
                    } else {
                        log.warn("Версия сохраняемой сущности меньше либо равна версии этой сущности, хранящейся в поисковом сервисе." +
                                " Сохранение отменено. [EntityName = {}, entityId = {}, NewVersionValue = {}, CurrentVersionValue = {}]",
                            entityName, entityId, entityExternalVersion, storedEntityExternalVersion);
                        resultType = ActionResultType.SUCCESS;
                    }
                }
            } while (resultType != ActionResultType.SUCCESS);
        }
    }

    private <T> void saveNestedEntity(T entity) throws Exception {
        ActionResultType resultType;
        NestedEntityModificationContext ctx = createContext(entity);
        do {
            InternalEntityHolder<?> rootHolder = getRootHolder(entity, ctx);
            if (rootHolder == null) {
                throw new IllegalStateException("Для вложенной сущности не найден объект сущности внешнего уровня");
            }
            InternalEntityHolder<?> mergedHolder = mergeNestedEntity(rootHolder, entity, ctx);
            resultType = facade.save(mergedHolder);
            if (resultType == ActionResultType.VERSION_CONFLICT) {
                log.warn("При сохранении вложенной сущности возник конфликт внутренней версии сущности верхнего уровня " +
                    "[Класс вложенной сущности = {}, Наименование сущности верхнего уровня = {}, ID сущности верхнего уровня = {}]." +
                    " Попытка сохранения будет произведена повторно", entity.getClass(), rootHolder.getEntityName(), rootHolder.getEntityId());
            }
        } while (resultType != ActionResultType.SUCCESS);
    }

    private <T> InternalEntityHolder<?> getRootHolder(T entity, NestedEntityModificationContext ctx) throws Exception {
        @SuppressWarnings("unchecked")
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata((Class<T>) entity.getClass());
        Class<?> outerEntityClass = ctx.outerEntityClass;
        List<Class<?>> outerEntityClassHierarchy = ctx.outerEntityClassHierarchy;
        log.debug("Сущность является вложенной для класса {}", outerEntityClass);
        log.debug("Иерархия родительских сущностей: {}", outerEntityClassHierarchy);
        Class<?> topLevelClass = outerEntityClassHierarchy.get(outerEntityClassHierarchy.size() - 1);
        EntityMetadataHolder<?> topLevelEntityMetadataHolder = metadataProvider.getMetadata(topLevelClass);
        String topLevelEntityName = topLevelEntityMetadataHolder.getEntityName();
        @SuppressWarnings("unchecked")
        String outerEntityId = entityMetadata.getOuterEntityIdMapper().map(entityMetadata.getOuterEntityIdProperty().getReadMethod().invoke(entity));
        if (topLevelClass == outerEntityClass) {
            return facade.get(topLevelClass, topLevelEntityName, outerEntityId, null);
        } else {
            EntityMetadataHolder<?> outerEntityMetadataHolder = ctx.outerEntityMetadataHolder;
            StringBuilder fieldName = new StringBuilder(outerEntityMetadataHolder.getEntityIdProperty().getName());
            for (int i = 1; i < outerEntityClassHierarchy.size(); i++) {
                Class<?> nextLevelEntityClass = outerEntityClassHierarchy.get(i);
                EntityMetadataHolder<?> nextLevelEntityMetadata = metadataProvider.getMetadata(nextLevelEntityClass);
                PropertyDescriptor nestedProperty = getPropertyByType(nextLevelEntityMetadata, outerEntityClassHierarchy.get(i - 1)).descriptor;
                fieldName.insert(0, nestedProperty.getName() + ".");
            }
            return getHolderByCondition(topLevelClass, topLevelEntityName, Condition.equal(fieldName.toString(), outerEntityId));
        }
    }

    private <T> InternalEntityHolder<?> mergeNestedEntity(InternalEntityHolder<?> rootHolder, T entity, NestedEntityModificationContext ctx) throws InvocationTargetException, IllegalAccessException {
        @SuppressWarnings("unchecked")
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata((Class<T>) entity.getClass());
        List<Class<?>> outerEntityClassHierarchy = ctx.outerEntityClassHierarchy;
        List<PropertyDescriptor> propertiesChain = new ArrayList<>(outerEntityClassHierarchy.size());
        for (int i = 1; i < outerEntityClassHierarchy.size(); i++) {
            Class<?> nextLevelEntityClass = outerEntityClassHierarchy.get(i);
            EntityMetadataHolder<?> nextLevelEntityMetadata = metadataProvider.getMetadata(nextLevelEntityClass);
            PropertyDescriptor nestedProperty = getPropertyByType(nextLevelEntityMetadata, outerEntityClassHierarchy.get(i - 1)).descriptor;
            propertiesChain.add(0, nestedProperty);
        }

        Object outerEntity = rootHolder.getEntity();
        for (PropertyDescriptor descriptor : propertiesChain) {
            outerEntity = descriptor.getReadMethod().invoke(outerEntity);
        }
        EntityPropertyData propertyData = getPropertyByType(ctx.outerEntityMetadataHolder, entity.getClass());
        PropertyDescriptor entityProperty = propertyData.descriptor;
        if (!propertyData.isCollection) {
            entityProperty.getWriteMethod().invoke(outerEntity, entity);
        } else {
            PropertyDescriptor entityIdPropertyDescriptor = entityMetadata.getEntityIdProperty();
            Object value = entityIdPropertyDescriptor.getReadMethod().invoke(entity);
            @SuppressWarnings("unchecked")
            String entityId = entityMetadata.getEntityIdMapper().map(value);
            @SuppressWarnings("unchecked")
            Collection<T> entityCollection = (Collection<T>) entityProperty.getReadMethod().invoke(outerEntity);
            List<T> resultCollection = new ArrayList<>();
            if (entityCollection != null) {
                resultCollection.addAll(entityCollection);
            }
            boolean replaced = false;
            for (int i = 0; i < resultCollection.size(); i++) {
                Object itemIdValue = entityIdPropertyDescriptor.getReadMethod().invoke(resultCollection.get(i));
                @SuppressWarnings("unchecked")
                String itemId = entityMetadata.getEntityIdMapper().map(itemIdValue);
                if (itemId.equals(entityId)) {
                    resultCollection.set(i, entity);
                    replaced = true;
                    break;
                }
            }
            if (!replaced) {
                resultCollection.add(entity);
            }
            entityProperty.getWriteMethod().invoke(outerEntity, resultCollection);
        }
        log.debug("Сущность для сохранения {}", rootHolder.getEntity());
        return rootHolder;
    }

    private <T> InternalEntitySearchQuery<T> map(EntityQuery<T> query) {
        EntityMetadataHolder<T> metadata = metadataProvider.getMetadata(query.getEntityClass());
        InternalEntitySearchQuery.Builder<T> builder = InternalEntitySearchQuery.builder(query.getEntityClass(), metadata.getEntityName())
            .queryString(query.getQueryString())
            .maxResultCount(query.getMaxResultCount())
            .startSearchFrom(query.getStartSearchFrom())
            .queryableFields(metadata.getFlatQueryableFields());

        Collection<Condition> conditions = query.getConditions();
        if (conditions != null) {
            builder.conditions(conditions.stream().map(conditionMapper::map).collect(Collectors.toList()));
            Collection<String> routingValues = extractRoutingValues(conditions, metadata);
            if (routingValues != null) {
                builder.routingValues(routingValues);
            }
        }

        Collection<SortField> sorting = query.getSorting();
        if (!CollectionUtils.isEmpty(sorting)) {
            Map<String, String> sortableField = metadata.getSortableField();
            List<QuerySorting> querySorting =
                sorting.stream()
                    .filter(sortField -> sortableField.containsKey(sortField.getFieldName()))
                    .map(sortField -> {
                        SortOrder sortOrder = SortOrder.valueOf(sortField.getOrderType());
                        String fieldName = sortableField.get(sortField.getFieldName());
                        if ("score".equals(fieldName)) {
                            return new ScoreQuerySorting(sortOrder);
                        }
                        return new FieldQuerySorting(fieldName, sortOrder);
                    })
                    .collect(Collectors.toList());
            builder.sorting(querySorting);
        }

        Collection<SearchAfter> searchAfterList = query.getSearchAfter();
        if (!CollectionUtils.isEmpty(searchAfterList)) {
            if (searchAfterList.size() != sorting.size()) {
                throw new IllegalArgumentException("Количество атрибутов searchAfter должно быть равно количеству атрибутов sorting." +
                    " searchAfter: " + searchAfterList.size() + " sorting: " + sorting.size());
            }
            Collection<Object> searchAfterValues = new ArrayList<>(searchAfterList.size());
            sorting.forEach(sortField -> searchAfterList.forEach(searchAfter -> {
                if (searchAfter.getValue() == null) {
                    throw new IllegalArgumentException("Атрибут searchAfter " + searchAfter.getSortFieldName() +
                        " имеет недопустимое значение null");
                }
                if (searchAfter.getSortFieldName().equals(sortField.getFieldName())) {
                    searchAfterValues.add(searchAfter.getValue());
                }
            }));
            if (searchAfterValues.size() != sorting.size()) {
                throw new IllegalArgumentException("Наименование поля, одного или нескольких атрибутов searchAfter, отсутствуют" +
                    " среди полей сортировки");
            }
            builder.searchAfterValues(searchAfterValues);
        }
        return builder.build();
    }

    private <T> QueryResult<T> map(InternalEntitySearchResult<T> searchResult) {
        return QueryResult.builder(searchResult.getEntityClass())
            .entities(searchResult.getEntities().stream().map(InternalEntityHolder::getEntity).collect(Collectors.toList()))
            .duration(searchResult.getDuration())
            .totalResultCount(searchResult.getTotalResultCount())
            .searchFailure(searchResult.getSearchFailure())
            .build();
    }

    /**
     * Извлечь значения routing'а для исполнения запроса из коллекции ограничений запроса.
     * <p>Значения определяются по накладываемым условиям:
     * если среди условий есть условие по полю, по которому определяется значение routing'а для объектов сущности,
     * с типом EqualCondition или InCondition, то значениями routing'а принимаются значения этих условий
     *
     * @param conditions коллекция ограничений запроса
     * @param metadata   метаданные класса искомой сущности
     */
    private Collection<String> extractRoutingValues(Collection<Condition> conditions, EntityMetadataHolder<?> metadata) {
        PropertyDescriptor routingValueProperty = metadata.getRoutingValueProperty();
        if (routingValueProperty != null) {
            String routingValueFieldName = routingValueProperty.getName();
            Collection<String> routingValues = conditions.stream()
                .filter(condition -> condition instanceof InCondition || condition instanceof EqualCondition)
                .filter(condition -> {
                    String fieldName;
                    if (condition instanceof InCondition) {
                        fieldName = ((InCondition<?>) condition).getFieldName();
                    } else {
                        fieldName = ((EqualCondition<?>) condition).getFieldName();
                    }
                    return fieldName.equals(routingValueFieldName);
                })
                .flatMap(condition -> {
                    if (condition instanceof InCondition) {
                        return ((InCondition<?>) condition).getValues().stream();
                    } else {
                        return Stream.of(((EqualCondition) condition).getValue());
                    }
                })
                .map(value -> {
                    @SuppressWarnings("unchecked")
                    String routingValue = value.getClass() == String.class ? (String) value : metadata.getRoutingValueMapper().map(value);
                    return routingValue;
                })
                .collect(Collectors.toList());
            if (!routingValues.isEmpty()) {
                return routingValues;
            }
        }
        return Collections.emptyList();
    }

    private List<Class<?>> getOuterEntityClassHierarchy(Class<?> lowLevelClass) {
        List<Class<?>> result = new ArrayList<>();
        Class<?> root = lowLevelClass;
        do {
            result.add(root);
            root = metadataProvider.getMetadata(root).getOuterEntityClass();
        } while (root != null);
        return result;
    }

    private <T> InternalEntityHolder<T> getHolderByCondition(Class<T> topLevelClass, String topLevelEntityName, Condition condition) throws Exception {
        InternalEntityHolder<T> rootHolder;
        InternalEntitySearchQuery<T> query = InternalEntitySearchQuery.builder(topLevelClass, topLevelEntityName)
            .maxResultCount(1)
            .conditions(Collections.singletonList(conditionMapper.map(condition)))
            .build();
        InternalEntitySearchResult<T> searchResult = facade.find(query);
        if (searchResult.getSearchFailure() != null) {
            throw searchResult.getSearchFailure();
        }
        if (searchResult.getTotalResultCount() > 1) {
            throw new RuntimeException("Найдено более одного результата [entityName = " + topLevelEntityName + ", condition = " + condition + "]");
        }
        rootHolder = searchResult.getEntities().iterator().next();
        if (rootHolder != null && rootHolder.getRoutingValue() == null) {
            // если клиент Elastic'а не возвращает в результатах поиска значение routing'а,
            // то приходится получать самостоятельно
            String routingValue = entityHolderConverter.convert(rootHolder.getEntity()).getRoutingValue();
            return InternalEntityHolder.builder(rootHolder).routingValue(routingValue).build();
        }
        return rootHolder;
    }


    private EntityPropertyData getPropertyByType(EntityMetadataHolder<?> metadataHolder, Class<?> propertyType) {
        boolean isCollection = false;
        List<PropertyDescriptor> descriptors = metadataHolder.getEntityProperties().get(propertyType);
        if (descriptors == null || descriptors.isEmpty()) {
            descriptors = metadataHolder.getEntityCollectionProperties().get(propertyType);
            isCollection = true;
        }
        if (descriptors == null || descriptors.isEmpty()) {
            throw new IllegalStateException("Для сущности " + metadataHolder.getEntityClass() + " не найдено поле с типом " + propertyType);
        } else if (descriptors.size() > 1) {
            throw new IllegalStateException("Для сущности " + metadataHolder.getEntityClass() + "  найдено более одного поля с типом " + propertyType);
        }
        return new EntityPropertyData(descriptors.get(0), isCollection);
    }

    private <T> NestedEntityModificationContext createContext(T entity) {
        @SuppressWarnings("unchecked")
        EntityMetadataHolder<T> entityMetadata = metadataProvider.getMetadata((Class<T>) entity.getClass());
        Class<?> outerEntityClass = entityMetadata.getOuterEntityClass();
        List<Class<?>> outerEntityClassHierarchy = getOuterEntityClassHierarchy(outerEntityClass);
        EntityMetadataHolder<?> outerEntityMetadataHolder = metadataProvider.getMetadata(outerEntityClass);
        return new NestedEntityModificationContext(outerEntityClass, outerEntityClassHierarchy, outerEntityMetadataHolder);
    }

    private static class EntityPropertyData {
        private final PropertyDescriptor descriptor;
        private final boolean isCollection;

        private EntityPropertyData(PropertyDescriptor descriptor, boolean isCollection) {
            this.descriptor = descriptor;
            this.isCollection = isCollection;
        }
    }

    private static class NestedEntityModificationContext {
        private final Class<?> outerEntityClass;
        private final List<Class<?>> outerEntityClassHierarchy;
        private final EntityMetadataHolder<?> outerEntityMetadataHolder;

        private NestedEntityModificationContext(Class<?> outerEntityClass, List<Class<?>> outerEntityClassHierarchy, EntityMetadataHolder<?> outerEntityMetadataHolder) {
            this.outerEntityClass = outerEntityClass;
            this.outerEntityClassHierarchy = outerEntityClassHierarchy;
            this.outerEntityMetadataHolder = outerEntityMetadataHolder;
        }
    }
}
