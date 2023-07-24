package ru.sberbank.pprb.sbbol.global_search.facade.query;

import org.apache.commons.lang3.StringUtils;
import ru.sberbank.pprb.sbbol.global_search.facade.search.InternalEntitySearchQuery;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * Реализация extractor'а токенов из поискового запроса
 */
public class SearchFilterTokenExtractorImpl implements SearchFilterTokenExtractor {

    @Override
    public Collection<String> extract(InternalEntitySearchQuery<?> entitySearchQuery) {
        String query = entitySearchQuery.getQueryString();
        if (query != null) {
            return Arrays.asList(StringUtils.splitByWholeSeparator(query, null));
        } else {
            return Collections.emptyList();
        }
    }
}
