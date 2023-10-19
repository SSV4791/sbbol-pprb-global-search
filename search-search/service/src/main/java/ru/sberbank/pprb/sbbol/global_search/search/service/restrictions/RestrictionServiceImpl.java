package ru.sberbank.pprb.sbbol.global_search.search.service.restrictions;


import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.search.model.Restriction;
import ru.sberbank.pprb.sbbol.global_search.search.model.RestrictionCheckResult;
import ru.sberbank.pprb.sbbol.global_search.search.model.restrictions.RestrictedAccess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

/**
 * Реализация сервиса работы с ограничениями поискового фильтра
 */
public class RestrictionServiceImpl implements RestrictionService {

    private final ConcurrentMap<Class<?>, Collection<Class<? extends Restriction>>> MANDATORY_RESTRICTION_CACHE = new ConcurrentHashMap<>();

    private final ConcurrentMap<Class<?>, Collection<Class<? extends Restriction>>> ADDITIONAL_RESTRICTION_CACHE = new ConcurrentHashMap<>();

    @Override
    public RestrictionCheckResult checkFilterEntityRestrictions(Collection<Restriction> filterRestrictions, Class<? extends BaseSearchableEntity> entityClass) {
        Collection<Class<? extends Restriction>> filterRestrictionTypes = filterRestrictions.stream()
            .map(Restriction::getClass)
            .collect(Collectors.toList());
        Collection<Class<? extends Restriction>> entityMandatoryRestrictions =
            MANDATORY_RESTRICTION_CACHE.computeIfAbsent(entityClass, clazz -> {
                Collection<RestrictedAccess> restrictedAccesses = getRestrictedAccessForClassHierarchy(clazz);
                Collection<Class<? extends Restriction>> restrictions = restrictedAccesses.stream()
                    .flatMap(restrictedAccess -> Arrays.stream(restrictedAccess.mandatoryRestrictions()))
                    .collect(Collectors.toSet());
                return Collections.unmodifiableCollection(restrictions);
            });
        if (!filterRestrictionTypes.containsAll(entityMandatoryRestrictions)) {
            return RestrictionCheckResult.NOT_ENOUGH_MANDATORY_RESTRICTIONS;
        }
        Collection<Class<? extends Restriction>> entityAdditionalRestrictions =
            ADDITIONAL_RESTRICTION_CACHE.computeIfAbsent(entityClass, clazz -> {
                Collection<RestrictedAccess> restrictedAccesses = getRestrictedAccessForClassHierarchy(clazz);
                Collection<Class<? extends Restriction>> restrictions = restrictedAccesses.stream()
                    .flatMap(restrictedAccess -> Arrays.stream(restrictedAccess.additionalRestrictions()))
                    .collect(Collectors.toSet());
                return Collections.unmodifiableCollection(restrictions);
            });

        boolean onlyAllowedRestrictions = filterRestrictionTypes.stream()
            .filter(restriction -> !entityMandatoryRestrictions.contains(restriction))
            .allMatch(entityAdditionalRestrictions::contains);
        if (!onlyAllowedRestrictions) {
            return RestrictionCheckResult.UNKNOWN_RESTRICTION;
        }

        return RestrictionCheckResult.OK;
    }

    private Collection<RestrictedAccess> getRestrictedAccessForClassHierarchy(Class<?> entityClass) {
        Collection<RestrictedAccess> result = new ArrayList<>();
        for (Class<?> cls = entityClass; !cls.equals(Object.class); cls = cls.getSuperclass()) {
            RestrictedAccess restrictedAccess = cls.getAnnotation(RestrictedAccess.class);
            if (restrictedAccess != null) {
                result.add(restrictedAccess);
            }
        }
        return result;
    }
}
