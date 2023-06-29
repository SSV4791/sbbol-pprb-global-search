package ru.sberbank.pprb.sbbol.global_search.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;

@SpringBootApplication(exclude = HibernateJpaAutoConfiguration.class)
public class SearchTaskRunnerApplication {

    public static void main(String[] args) {
        SpringApplication.run(SearchTaskRunnerApplication.class, args);
    }

}
