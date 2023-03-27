package ru.sberbank.pprb.sbbol.global_search.search.config;

import org.slf4j.MDC;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Configuration
@ComponentScan(
    basePackages = {
        "ru.sberbank.pprb.sbbol.global_search.engine.config",
        "ru.sberbank.pprb.sbbol.global_search.facade.config",
        "ru.sberbank.pprb.sbbol.global_search.search.config",
    }
)
public class SearchRunnerConfiguration {

    @Bean
    GenericFilterBean genericFilterBean() {
        return new OncePerRequestFilter() {
            @Override
            protected void doFilterInternal(
                HttpServletRequest request,
                HttpServletResponse response,
                FilterChain filterChain
            ) throws ServletException, IOException {
                MDC.put("requestUid", request.getHeader("x-request-id"));
                try {
                    filterChain.doFilter(request, response);
                } finally {
                    MDC.remove("requestUid");
                }
            }
        };
    }
}
