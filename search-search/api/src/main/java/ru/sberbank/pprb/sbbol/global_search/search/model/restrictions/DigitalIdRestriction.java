package ru.sberbank.pprb.sbbol.global_search.search.model.restrictions;

import java.util.Collection;

/**
 * Ограничение поискового фильтра налагаемое на доступ к организации
 */
public class DigitalIdRestriction implements Restriction {

    private Collection<String> digitalId;

    public Collection<String> getDigitalId() {
        return digitalId;
    }

    public void setDigitalId(Collection<String> digitalId) {
        this.digitalId = digitalId;
    }
}

