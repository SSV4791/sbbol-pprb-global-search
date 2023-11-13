package ru.sberbank.pprb.sbbol.global_search.producer.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchEvent<T extends BaseSearchableEntity> {

    private T searchableEntity;

    private ActionType actionType;
}
