package io.github.alaugks.spring.messagesource.catalog.catalog;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

abstract class CatalogAbstract implements CatalogInterface {

    protected CatalogInterface nextHandler;

    public CatalogInterface nextHandler(CatalogInterface handler) {
        this.nextHandler = handler;
        return this;
    }

    @Override
    public Map<Locale, Map<String, String>> getAll() {
        if (this.nextHandler == null) {
            return new ConcurrentHashMap<>();
        }

        return this.nextHandler.getAll();
    }

    @Override
    public String resolveCode(Locale locale, String code) {
        if (this.nextHandler == null) {
            return null;
        }

        return this.nextHandler.resolveCode(locale, code);
    }

    @Override
    public void build() {
        if (this.nextHandler != null) {
            this.nextHandler.build();
        }
    }
}
