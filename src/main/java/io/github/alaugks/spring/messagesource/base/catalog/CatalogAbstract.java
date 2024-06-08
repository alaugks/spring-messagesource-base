package io.github.alaugks.spring.messagesource.base.catalog;

import io.github.alaugks.spring.messagesource.base.records.TransUnitCatalog;
import java.util.List;
import java.util.Locale;

abstract class CatalogAbstract implements CatalogInterface {

    protected CatalogInterface nextHandler;

    public CatalogInterface nextHandler(CatalogInterface handler) {
        this.nextHandler = handler;
        return this;
    }

    @Override
    public List<TransUnitCatalog> getAll() {
        if (this.nextHandler == null) {
            return List.of();
        }

        return this.nextHandler.getAll();
    }

    @Override
    public String get(Locale locale, String code) {
        if (this.nextHandler == null) {
            return null;
        }

        return this.nextHandler.get(locale, code);
    }

    @Override
    public void build() {
        if (this.nextHandler != null) {
            this.nextHandler.build();
        }
    }

    protected String localeToLocaleKey(Locale locale) {
        Locale.Builder localeBuilder = new Locale.Builder();
        localeBuilder.setLanguage(locale.getLanguage());
        if (!locale.getCountry().isEmpty()) {
            localeBuilder.setRegion(locale.getCountry());
        }
        return normalizeLocaleKey(localeBuilder.build().toString());
    }

    protected String normalizeLocaleKey(String langCode) {
        return langCode.toLowerCase().replace("_", "-");
    }
}
