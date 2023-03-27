package ru.sberbank.pprb.sbbol.global_search.facade.entity;

import java.util.Objects;

/**
 * Внутреннее представление объекта сущности, используемой в поисковом сервисе
 *
 * @param <T> тип объекта сущности
 */
public class InternalEntityHolder<T> {

    /**
     * объект сущности
     */
    private final T entity;

    /**
     * класс объекта сущности
     */
    private final Class<T> entityClass;

    /**
     * наименование сущности
     */
    private final String entityName;

    /**
     * наименование поискового индекса сущности
     */
    private final String indexName;

    /**
     * идентификатор сущности
     */
    private final String entityId;

    /**
     * значение поля routing'а
     */
    private final String routingValue;

    /**
     * Последовательный номер операции в OpenSearch
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.x/optimistic-concurrency-control.html">
     * OpenSearch optimistic concurrency control</a>
     */
    private final Long seqNo;

    /**
     * Primary шард OpenSearch, координирующий операцию
     *
     * @see <a href="https://www.elastic.co/guide/en/elasticsearch/reference/7.x/optimistic-concurrency-control.html">
     * OpenSearch optimistic concurrency control</a>
     */
    private final Long primaryTerm;

    /**
     * Класс сущности, внешней по отношению к данной (для вложенных сущностей)
     */
    private final Class<?> outerEntityClass;

    /**
     * Идентификатор сущности, внешней по отношению к данной
     */
    private final String outerEntityId;

    private InternalEntityHolder(Builder<T> builder) {
        entityClass = builder.entityClass;
        entityName = builder.entityName;
        indexName = builder.indexName;
        entity = builder.entity;
        entityId = builder.entityId;
        routingValue = builder.routingValue;
        seqNo = builder.seqNo;
        primaryTerm = builder.primaryTerm;
        outerEntityClass = builder.outerEntityClass;
        outerEntityId = builder.outerEntityId;
    }

    /**
     * Получить билдер внутреннего представления объекта сущности, используемой в поисковом сервисе
     *
     * @param entityClass класс объекта сущности
     * @param entityName  наименование сущности
     * @param entity      объект сущности
     * @param indexName   наименование поискового индекса сущности
     * @param entityId    идентификатор сущности
     * @param <T>         тип объекта сущности
     */
    public static <T> Builder<T> builder(Class<T> entityClass, String entityName, T entity, String indexName, String entityId) {
        return new Builder<>(entityClass, entityName, entity, indexName, entityId);
    }

    /**
     * Получить билдер внутреннего представления объекта сущности, используемой в поисковом сервисе, из другого внутреннего представления объекта сущности
     *
     * @param holder копируемый объект внутреннего представления объекта сущности
     * @param <T>    тип объекта сущности
     */
    public static <T> Builder<T> builder(InternalEntityHolder<T> holder) {
        return new Builder<>(holder.getEntityClass(), holder.getEntityName(), holder.getEntity(), holder.getIndexName(), holder.getEntityId())
            .routingValue(holder.getRoutingValue())
            .seqNo(holder.getSeqNo())
            .primaryTerm(holder.getPrimaryTerm())
            .outerEntityClass(holder.getOuterEntityClass())
            .outerEntityId(holder.getOuterEntityId());
    }

    public T getEntity() {
        return entity;
    }

    public Class<T> getEntityClass() {
        return entityClass;
    }

    public String getEntityName() {
        return entityName;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getRoutingValue() {
        return routingValue;
    }

    public Long getSeqNo() {
        return seqNo;
    }

    public Long getPrimaryTerm() {
        return primaryTerm;
    }

    public String getIndexName() {
        return indexName;
    }

    public Class<?> getOuterEntityClass() {
        return outerEntityClass;
    }

    public String getOuterEntityId() {
        return outerEntityId;
    }

    @Override
    public String toString() {
        return "InternalEntityHolder{" +
            "entity=" + entity +
            ", entityClass=" + entityClass +
            ", entityName='" + entityName + '\'' +
            ", indexName='" + indexName + '\'' +
            ", entityId='" + entityId + '\'' +
            ", routingValue='" + routingValue + '\'' +
            ", seqNo=" + seqNo +
            ", primaryTerm=" + primaryTerm +
            ", outerEntityClass=" + outerEntityClass +
            ", outerEntityId='" + outerEntityId + '\'' +
            '}';
    }

    /**
     * Билдер внутреннего представление объекта сущности, используемой в поисковом сервисе
     *
     * @param <T> тип объекта сущности
     */
    public static final class Builder<T> {

        private final Class<T> entityClass;

        private final String entityName;

        private final T entity;

        private final String indexName;

        private final String entityId;

        private String routingValue;

        private Long seqNo;

        private Long primaryTerm;

        private Class<?> outerEntityClass;

        private String outerEntityId;

        public Builder(Class<T> entityClass, String entityName, T entity, String indexName, String entityId) {
            this.entityClass = Objects.requireNonNull(entityClass);
            this.entityName = Objects.requireNonNull(entityName);
            this.entity = Objects.requireNonNull(entity);
            this.indexName = Objects.requireNonNull(indexName);
            this.entityId = Objects.requireNonNull(entityId);
        }

        public Builder<T> routingValue(String routingValue) {
            this.routingValue = routingValue;
            return this;
        }

        public Builder<T> seqNo(Long seqNo) {
            this.seqNo = seqNo;
            return this;
        }

        public Builder<T> primaryTerm(Long primaryTerm) {
            this.primaryTerm = primaryTerm;
            return this;
        }

        public Builder<T> outerEntityClass(Class<?> outerEntityClass) {
            this.outerEntityClass = outerEntityClass;
            return this;
        }

        public Builder<T> outerEntityId(String outerEntityId) {
            this.outerEntityId = outerEntityId;
            return this;
        }

        public InternalEntityHolder<T> build() {
            return new InternalEntityHolder<>(this);
        }
    }
}
