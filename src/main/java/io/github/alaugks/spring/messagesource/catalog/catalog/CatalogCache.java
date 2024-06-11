package io.github.alaugks.spring.messagesource.catalog.catalog;

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class CatalogCache extends CatalogAbstract {

    private final Map<String, Map<String, String>> messagesMapCaches = new ConcurrentHashMap<>();

    @Override
    public String resolve(Locale locale, String code) {
        if (locale.toString().isEmpty() || code.isEmpty()) {
            return null;
        }

        // Resolve in Cache
        String value;
        Map<String, String> messagesMap = this.messagesMapCaches.get(this.localeToLocaleKey(locale));
        if (messagesMap != null) {
            value = messagesMap.get(code);
            if (value != null) {
                return value;
            }
        }

        // Resolve in Catalog
        value = super.resolve(locale, code);
        if (value != null) {
            this.put(locale, code, value);
        }

        return value;
    }

    @Override
    public void build() {
        super.build();
        this.messagesMapCaches.putAll(super.getAll());
    }

    private void put(Locale locale, String code, String targetValue) {
        if (!locale.toString().isEmpty() && !code.isEmpty()) {
            this.messagesMapCaches.putIfAbsent(super.localeToLocaleKey(locale), new ConcurrentHashMap<>());
            this.messagesMapCaches.get(super.localeToLocaleKey(locale)).put(code, targetValue);
        }
    }
}
