package ru.sberbank.pprb.sbbol.global_search.search;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchFilter;
import ru.sberbank.pprb.sbbol.global_search.search.model.SearchResponse;
import ru.sberbank.pprb.sbbol.global_search.search.service.SearchService;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/search")
public class SearchController {

    private static final String FIND_ALL = "/find";

    private final SearchService service;

    public SearchController(SearchService service) {
        this.service = service;
    }

    @PostMapping(value = FIND_ALL)
    public SearchResponse findAll(@RequestBody SearchFilter filter) throws IOException {
        return service.find(filter);
    }
}
