package io.github.alaugks.spring.messagesource.base.catalog;

import java.util.Locale;
import java.util.Map;

public interface CatalogHandlerInterface {

    Map<String, Map<String, String>> getAll();

    String get(Locale locale, String code);
}
