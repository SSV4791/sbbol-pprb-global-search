package ru.sberbank.pprb.sbbol.global_search.sink;

/**
 * Сообщение об изменении объекта сущности, доступной в полнотекстовом поиске
 */
public class SearchableEntityMessageInfo<T> {

    /**
     * Ключ сообщения
     */
    private final MessageKey key;

    /**
     * Тело сообщения
     */
    private final MessagePayload<T> message;

    public SearchableEntityMessageInfo(MessageKey key, MessagePayload<T> message) {
        this.key = key;
        this.message = message;
    }

    public MessageKey getKey() {
        return key;
    }

    public MessagePayload<T> getMessage() {
        return message;
    }

    @Override
    public String toString() {
        return "SearchableEntityMessageInfo{" +
            "key=" + key +
            ", message=" + message +
            '}';
    }
}
