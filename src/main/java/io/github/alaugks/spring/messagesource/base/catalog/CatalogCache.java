package io.github.alaugks.spring.messagesource.base.catalog;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

public final class CatalogCache extends CatalogAbstract {

    private final Cache cache;

    public CatalogCache(Cache cache) {
        Assert.notNull(cache, "cache must not be null");

        this.cache = cache;
    }

    @Override
    public Map<String, Map<String, String>> getAll() {
        try {
            Map<String, Map<String, String>> result = new HashMap<>();
            Map<Object, Object> items = new HashMap<>((ConcurrentHashMap<?, ?>) this.cache.getNativeCache());
            items.forEach((code, value) -> {
                String[] split = code.toString().split("\\|");
                result.putIfAbsent(
                    split[0],
                    new HashMap<>()
                );
                result.get(split[0]).putIfAbsent(
                    split[1],
                    value.toString()
                );
            });
            return result;
        } catch (Exception e) {
            return super.getAll();
        }
    }

    @Override
    public String get(Locale locale, String code) {

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

        value = super.get(locale, code);
        if (value != null) {
            this.put(locale, code, value);
        }

        return value;
    }

    @Override
    public void build() {
        super.build();
        super.getAll().forEach((langCode, catalogDomain) -> catalogDomain.forEach((code, value) ->
            this.put(
                Locale.forLanguageTag(
                    super.normalizeLocaleKey(langCode)
                ),
                code,
                value
            )
        ));
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
