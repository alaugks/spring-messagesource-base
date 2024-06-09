package io.github.alaugks.spring.messagesource.base.catalog;

import io.github.alaugks.spring.messagesource.base.records.TransUnitCatalog;
import java.util.List;
import java.util.Locale;

public interface CatalogInterface {

    CatalogInterface nextHandler(CatalogInterface handler);

    List<TransUnitCatalog> getAll();

    String resolve(Locale locale, String code);

    void build();
}
