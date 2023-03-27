package ru.sberbank.pprb.sbbol.global_search.engine.query.condition;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Условие соответствия списку идентификаторов
 */
public final class IdsCondition implements Condition {

    /**
     * Значения идентификатора, удовлетворяющие условию
     */
    private final Collection<String> ids;

    public IdsCondition(Collection<String> ids) {
        this.ids = new ArrayList<>(ids);
    }

    public Collection<String> getIds() {
        return ids;
    }

    @Override
    public String toString() {
        return "IdsCondition{" +
            "ids=" + ids +
            '}';
    }
}
