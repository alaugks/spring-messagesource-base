package io.github.alaugks.spring.messagesource.catalog.catalog;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnitCatalog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

public final class CatalogCache extends CatalogAbstract {

    private final Cache cache;

    public CatalogCache(Cache cache) {
        Assert.notNull(cache, "Argument cache must not be null");
        this.cache = cache;
    }

    @Override
    public List<TransUnitCatalog> getAll() {
        try {
            List<TransUnitCatalog> translations = new ArrayList<>();
            Map<Object, Object> items = new HashMap<>((ConcurrentHashMap<?, ?>) this.cache.getNativeCache());
            items.forEach((code, value) -> {
                String[] split = code.toString().split("\\|");
                translations.add(
                    new TransUnitCatalog(Locale.forLanguageTag(split[0]), split[1], value.toString())
                );
            });
            return translations;
        } catch (Exception e) {
            return super.getAll();
        }
    }

    @Override
    public String resolve(Locale locale, String code) {

        if (locale.toString().isEmpty() || code.isEmpty()) {
            return null;
        }

        String value = null;
        Cache.ValueWrapper valueWrapper = this.cache.get(this.createCacheKey(locale, code));
        if (valueWrapper != null) {
            value = Objects.requireNonNull(valueWrapper.get()).toString();
        }

        if (value != null) {
            return value;
        }

        value = super.resolve(locale, code);
        if (value != null) {
            this.put(locale, code, value);
        }

        return value;
    }

    @Override
    public void build() {
        super.build();
        super.getAll().forEach(translation ->
            this.put(
                Locale.forLanguageTag(
                    super.normalizeLocaleKey(translation.locale().toString())
                ),
                translation.code(),
                translation.value()
            )
        );
    }

    private void put(Locale locale, String code, String targetValue) {
        if (!locale.toString().isEmpty() && !code.isEmpty()) {
            this.cache.putIfAbsent(
                this.createCacheKey(locale, code),
                targetValue
            );
        }
    }

    private String createCacheKey(Locale locale, String code) {
        return super.localeToLocaleKey(locale) + "|" + code;
    }
}
