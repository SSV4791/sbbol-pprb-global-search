package ru.sberbank.pprb.sbbol.global_search.sink.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchClientFacade;
import ru.sberbank.pprb.sbbol.global_search.search.common.ActionType;
import ru.sberbank.pprb.sbbol.global_search.search.model.BaseSearchableEntity;
import ru.sberbank.pprb.sbbol.global_search.sink.mapper.SearchSinkMapper;
import ru.sberbank.pprb.sbbol.global_search.sink.service.SearchSinkService;

@Slf4j
@RequiredArgsConstructor
public class SearchSinkServiceImpl implements SearchSinkService {

    private static final Logger LOG = LoggerFactory.getLogger(SearchSinkServiceImpl.class);

    private final OpenSearchClientFacade openSearchClientFacade;

    private final SearchSinkMapper searchSinkMapper;

    @Override
    public void send(BaseSearchableEntity searchableEntity, String actionType) {
        LOG.debug("Запрос на отправку сущности {} c actionType: {} на индексацию ...", searchableEntity, actionType);
        try {
            var internalEntityHolder = searchSinkMapper.map(searchableEntity);
            LOG.debug("Отправляем сущность {} на индексацию ...", internalEntityHolder);
            var action = ActionType.valueOf(actionType);
            switch (action) {
                case SAVE -> openSearchClientFacade.save(internalEntityHolder);
                case DELETE -> openSearchClientFacade.delete(internalEntityHolder);
            }
        } catch (Exception ex) {
            log.debug("Ошибка при отправки сущности {} c actionType: {} на индексацию. StackTrace ошибки: {}",
                searchableEntity, actionType, ExceptionUtils.getStackTrace(ex));
        }
    }
}
