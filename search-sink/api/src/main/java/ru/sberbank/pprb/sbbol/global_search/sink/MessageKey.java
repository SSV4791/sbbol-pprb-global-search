package ru.sberbank.pprb.sbbol.global_search.sink;

import java.util.UUID;

/**
 * Ключ сообщения
 */
public class MessageKey {

    /**
     * Тип сущности, доступной для полнотекстового поиска
     */
    private Class<?> entityType;

    /**
     * Идентификатор записи
     */
    private UUID guid;

    public Class<?> getEntityType() {
        return entityType;
    }

    public void setEntityType(Class<?> entityType) {
        this.entityType = entityType;
    }

    public UUID getGuid() {
        return guid;
    }

    public void setGuid(UUID guid) {
        this.guid = guid;
    }

    @Override
    public String toString() {
        return "MessageKey{" +
            "entityType=" + entityType +
            ", guid=" + guid +
            '}';
    }
}
