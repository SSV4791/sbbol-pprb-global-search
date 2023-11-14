package ru.sberbank.pprb.sbbol.global_search.search.service.restrictions;


import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.Restriction;
import ru.sberbank.pprb.sbbol.global_search.search.model.RestrictionCheckResult;

import java.util.Collection;

/**
 * Сервис работы с ограничениями поискового фильтра
 */
public interface RestrictionService {

    /**
     * Проверить ограничения поискового фильтра для сущности.
     * <p>Ограничения проверяются по всей иерархии классов.
     *
     * @param filterRestrictions ограничения поискового фильтра
     * @param entityClass        класс сущности
     */
    RestrictionCheckResult checkFilterEntityRestrictions(Collection<Restriction> filterRestrictions, Class<? extends BaseSearchableEntity> entityClass);
}
