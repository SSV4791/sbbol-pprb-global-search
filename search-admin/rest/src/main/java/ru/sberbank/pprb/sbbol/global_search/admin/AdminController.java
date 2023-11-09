package ru.sberbank.pprb.sbbol.global_search.admin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminController {

    @GetMapping(value = "/admin")
    public void methodName() {
        throw new UnsupportedOperationException();
    }
}
