package ru.sberbank.pprb.sbbol.global_search.engine.query.condition.impl;

import com.google.common.collect.ImmutableMap;
import ru.sberbank.pprb.sbbol.global_search.engine.query.condition.*;

import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition.*;

/**
 * Реализация маппера условий запроса к поисковому сервису в условия к OpenSearch
 */
public class ConditionMapperImpl implements ConditionMapper {

    private final Map<Class<? extends Condition>, Function<Condition, ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition>> mappers =
        ImmutableMap.<Class<? extends Condition>, Function<Condition, ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition>>builder()
            .put(NotCondition.class, source -> {
                NotCondition condition = (NotCondition) source;
                return not(map(condition.getNestedCondition()));
            })
            .put(IdCondition.class, source -> {
                IdCondition condition = (IdCondition) source;
                return id(condition.getId());
            })
            .put(IdsCondition.class, source -> {
                IdsCondition condition = (IdsCondition) source;
                return ids(condition.getIds());
            })
            .put(EqualCondition.class, source -> {
                EqualCondition<?> condition = (EqualCondition<?>) source;
                return ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition.equals(condition.getFieldName(), condition.getValue());
            })
            .put(InCondition.class, source -> {
                InCondition<?> condition = (InCondition<?>) source;
                return in(condition.getFieldName(), condition.getValues());
            })
            .put(FieldExistsCondition.class, source -> {
                FieldExistsCondition condition = (FieldExistsCondition) source;
                return fieldExists(condition.getFieldName());
            })
            .put(GtCondition.class, source -> {
                GtCondition<?> condition = (GtCondition<?>) source;
                return gt(condition.getFieldName(), condition.getFrom());
            })
            .put(GteCondition.class, source -> {
                GteCondition<?> condition = (GteCondition<?>) source;
                return gte(condition.getFieldName(), condition.getFrom());
            })
            .put(LtCondition.class, source -> {
                LtCondition<?> condition = (LtCondition<?>) source;
                return lt(condition.getFieldName(), condition.getTo());
            })
            .put(LteCondition.class, source -> {
                LteCondition<?> condition = (LteCondition<?>) source;
                return lte(condition.getFieldName(), condition.getTo());
            })
            .put(AndCondition.class, source -> {
                AndCondition condition = (AndCondition) source;
                return and(condition.getNestedConditions().stream()
                    .map(this::map)
                    .collect(Collectors.toList())
                );
            })
            .put(OrCondition.class, source -> {
                OrCondition condition = (OrCondition) source;
                return or(condition.getNestedConditions().stream()
                    .map(this::map)
                    .collect(Collectors.toList())
                );
            })
            .build();

    @Override
    public ru.sberbank.pprb.sbbol.global_search.facade.query.condition.Condition map(Condition source) {
        return mappers.get(source.getClass()).apply(source);
    }
}
