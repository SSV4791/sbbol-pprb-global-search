package ru.sberbank.pprb.sbbol.global_search.sink.service.impl;

import lombok.extern.slf4j.Slf4j;
import ru.sberbank.pprb.sbbol.global_search.sink.service.IndexingService;

@Slf4j
public class IndexingServiceImpl implements IndexingService {

    @Override
    public void createSearchIndex(String document) {
        log.info("Индексирование документа: {}", document);
    }
}
