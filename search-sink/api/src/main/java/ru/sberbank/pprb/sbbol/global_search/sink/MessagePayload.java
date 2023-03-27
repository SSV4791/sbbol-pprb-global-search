package ru.sberbank.pprb.sbbol.global_search.sink;

//import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Содержимое сообщения об изменении объекта сущности, доступной в полнотекстовом поиске
 */
public class MessagePayload<T> {

    /**
     * Тип действия над сущностью, доступной в полнотекстовом поиске
     */
    private SearchableEntityActionType actionType;

    /**
     * Количество попыток отправки сообщения
     */
    private int count;

    /**
     * Тело сообщения
     */
//    @JsonTypeInfo(use=JsonTypeInfo.Id.CLASS, property="_class")
    private T message;

    public MessagePayload() {
        this.count = 1;
    }

    public SearchableEntityActionType getActionType() {
        return actionType;
    }

    public void setActionType(SearchableEntityActionType actionType) {
        this.actionType = actionType;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public T getMessage() {
        return message;
    }

    public void setMessage(T message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "MessagePayload{" +
            "actionType=" + actionType +
            ", count=" + count +
            ", message=" + message +
            '}';
    }

}
