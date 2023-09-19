package ru.sbrf.sbbol.search.updater;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;

public class PlaceholderResolver {

    @Autowired
    private Environment env;

    public String resolve(String stringWithPlaceholders) {
        return env.resolvePlaceholders(stringWithPlaceholders);
    }
}
