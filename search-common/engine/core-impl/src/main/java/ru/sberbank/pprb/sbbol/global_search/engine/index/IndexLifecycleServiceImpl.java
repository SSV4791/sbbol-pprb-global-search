package ru.sberbank.pprb.sbbol.global_search.engine.index;

import lombok.extern.slf4j.Slf4j;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataHolder;
import ru.sberbank.pprb.sbbol.global_search.engine.entity.metadata.EntityMetadataProvider;
import ru.sberbank.pprb.sbbol.global_search.facade.OpenSearchIndexApiFacade;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;

@Slf4j
public class IndexLifecycleServiceImpl implements IndexLifecycleService {

    private final OpenSearchIndexApiFacade indexApiFacade;

    private final EntityMetadataProvider metadataProvider;

    public IndexLifecycleServiceImpl(OpenSearchIndexApiFacade indexApiFacade, EntityMetadataProvider metadataProvider) {
        this.indexApiFacade = indexApiFacade;
        this.metadataProvider = metadataProvider;
    }

    @Override
    public <T> Collection<String> removeObsolete(Class<T> entityClass, Predicate<String> obsolescencePredicate) throws IOException {
        log.info("Удаление устаревших индексов сущности {}", entityClass.getName());
        Collection<String> result = new ArrayList<>();
        EntityMetadataHolder<T> metadataHolder = metadataProvider.getMetadata(entityClass);
        String entityName = metadataHolder.getEntityName();
        Collection<String> entityIndices = indexApiFacade.indexNames(entityName);
        for (String indexName : entityIndices) {
            if (obsolescencePredicate.test(indexName)) {
                boolean removed = indexApiFacade.removeIndex(indexName);
                if (removed) {
                    result.add(indexName);
                }
            }
        }
        if (!result.isEmpty()) {
            log.info("Удалены устаревшие индексы: {}", result);
        } else {
            log.info("Устаревших индексов не обнаружено");
        }
        return result;
    }
}
