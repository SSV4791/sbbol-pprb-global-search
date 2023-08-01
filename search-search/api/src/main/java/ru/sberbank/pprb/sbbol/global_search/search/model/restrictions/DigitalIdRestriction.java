package ru.sberbank.pprb.sbbol.global_search.search.model.restrictions;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

/**
 * Ограничение поискового фильтра налагаемое на доступ к организации
 */
@Getter
@Setter
public class DigitalIdRestriction implements Restriction {

    private Collection<String> digitalId;
}

