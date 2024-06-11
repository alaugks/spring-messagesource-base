package io.github.alaugks.spring.messagesource.catalog.catalog;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CatalogCache extends CatalogAbstract {

    private final Map<Locale, Map<String, String>> messagesMapCaches = new ConcurrentHashMap<>();

    @Override
    public String resolve(Locale locale, String code) {
        if (locale.toString().isEmpty() || code.isEmpty()) {
            return null;
        }

        // Resolve in Cache
        String value;
        Map<String, String> messagesMap = this.messagesMapCaches.get(locale);
        if (messagesMap != null) {
            value = messagesMap.get(code);
            if (value != null) {
                return value;
            }

            if (messagesMap.containsKey(code)) {
                return messagesMap.get(code);
            }
        }

        // Resolve in Catalog
        value = super.resolve(locale, code);

        // Put to cache
        this.put(locale, code, value);

        return value;
    }

    @Override
    public Map<Locale, Map<String, String>> getAll() {
        if (!this.messagesMapCaches.isEmpty()) {
            return this.messagesMapCaches;
        }

        return super.getAll();
    }

    @Override
    public void build() {
        super.build();
        this.messagesMapCaches.putAll(super.getAll());
    }

    private void put(Locale locale, String code, String targetValue) {
        if (!this.messagesMapCaches.containsKey(locale)) {
            return;
        }
        this.messagesMapCaches.putIfAbsent(locale, new ConcurrentHashMap<>());
        this.messagesMapCaches.get(locale).put(code, targetValue);
    }
}
