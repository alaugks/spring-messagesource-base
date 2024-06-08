package io.github.alaugks.spring.messagesource.base.catalog;

import io.github.alaugks.spring.messagesource.base.records.TransUnitCatalog;
import java.util.List;
import java.util.Locale;

public interface CatalogHandlerInterface {

    List<TransUnitCatalog> getAll();

    String get(Locale locale, String code);
}
