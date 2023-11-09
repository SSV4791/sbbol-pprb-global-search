package ru.sberbank.pprb.sbbol.global_search.task;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TaskController {

    @GetMapping(value = "/task")
    public void methodName() {
        throw new UnsupportedOperationException();
    }
}
