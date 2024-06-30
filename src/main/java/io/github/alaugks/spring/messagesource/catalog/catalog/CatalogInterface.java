package io.github.alaugks.spring.messagesource.catalog.catalog;

import java.util.Locale;
import java.util.Map;

public interface CatalogInterface {

    CatalogInterface nextHandler(CatalogInterface handler);

    Map<Locale, Map<String, String>> getAll();

    String resolveCode(Locale locale, String code);

    void build();
}
