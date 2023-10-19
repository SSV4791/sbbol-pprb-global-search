package ru.sberbank.pprb.sbbol.global_search.search;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchFilter;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchResponse;
import ru.sberbank.pprb.sbbol.global_search.search.service.SearchService;

@RestController
public class SearchController implements SearchApi {

    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }

    @Override
    public ResponseEntity<SearchResponse> find(SearchFilter searchFilter) {
        return ResponseEntity.ok(service.find(searchFilter));
    }
}
