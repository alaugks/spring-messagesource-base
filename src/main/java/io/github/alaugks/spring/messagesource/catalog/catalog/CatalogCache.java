package io.github.alaugks.spring.messagesource.catalog.catalog;

import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public final class CatalogCache extends CatalogAbstract {

    private final Map<Locale, Map<String, String>> cacheMap = new ConcurrentHashMap<>();

    @Override
    public String resolveCode(Locale locale, String code) {
        if (locale.toString().isEmpty() || code.isEmpty()) {
            return null;
        }

        // Resolve in Cache
        Optional<String> value = this.getTargetValue(locale, code);
        if(value.isPresent()) {
            return value.get();
        }

        // Resolve in Catalog
        String resolvedValue = super.resolveCode(locale, code);

        // Put to cache
        this.put(locale, code, resolvedValue);

        return resolvedValue;
    }

    @Override
    public Map<Locale, Map<String, String>> getAll() {
        if (!this.cacheMap.isEmpty()) {
            return this.cacheMap;
        }

        return super.getAll();
    }

    @Override
    public void build() {
        super.build();
        this.cacheMap.putAll(super.getAll());
    }

    private Optional<String> getTargetValue(Locale locale, String code) {
        Map<String, String> map = this.cacheMap.get(locale);
        if (map != null) {
            return Optional.ofNullable(map.get(code));
        }
        return Optional.empty();
    }

    private void put(Locale locale, String code, String targetValue) {
        this.cacheMap.computeIfAbsent(locale, k -> new ConcurrentHashMap<>()).put(code, targetValue);
    }
}
