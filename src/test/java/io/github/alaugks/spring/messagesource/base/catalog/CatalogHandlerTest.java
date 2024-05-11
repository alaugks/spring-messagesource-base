package io.github.alaugks.spring.messagesource.base.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.alaugks.spring.messagesource.base.records.Translation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

class CatalogHandlerTest {

    private final Locale locale = Locale.forLanguageTag("en");

    @Test
    void test_catalog() {
        String domain = "messages";
        String key = domain + ".key";
        List<Translation> translations = new ArrayList<>();
        translations.add(new Translation(domain, this.locale, "key", "from_base_catalog"));
        var baseCatalog = new Catalog(translations, this.locale, domain);

        var catalogHandler = CatalogHandler
            .builder()
            .addHandler(baseCatalog)
            .build();

        assertEquals("from_base_catalog", catalogHandler.get(this.locale, key));
        assertEquals("from_base_catalog", catalogHandler.getAll().get("en").get(key));
    }

    @Test
    void test_catalog_withCache() {
        String domain = "messages";
        String key = domain + ".key";
        String localeKey = "en|" + key;
        List<Translation> translations = new ArrayList<>();
        translations.add(new Translation(domain, this.locale, "key", "from_base_catalog"));
        var baseCatalog = new Catalog(translations, this.locale, domain);
        var cache = new ConcurrentMapCache("text-cache");
        var cacheCatalog = new CatalogCache(cache);

        // Is translation in cache?
        var cacheBuffer = cacheToArray(cache);
        assertNull(cacheBuffer.get(localeKey));

        var catalogHandler = CatalogHandler
            .builder()
            .addHandler(cacheCatalog)
            .addHandler(baseCatalog)
            .build();

        // Exists item in Cache after build?
        assertEquals("from_base_catalog", cacheToArray(cache).get(localeKey));

        // Hit
        assertEquals("from_base_catalog", catalogHandler.get(this.locale, key));

        // Remote item from Cache
        cache.evictIfPresent(localeKey);

        // Is item removed from cache?
        assertNull(cacheToArray(cache).get(localeKey));

        // Get from Catalog and put to Cache
        assertEquals("from_base_catalog", catalogHandler.get(this.locale, key));
        assertEquals("from_base_catalog", cacheToArray(cache).get(localeKey));

        // CatalogCache Hit
        // Overwrite cacheItem to test translation is from Cache
        cache.put(localeKey, "value_catalog_cache");
        assertEquals("value_catalog_cache", catalogHandler.get(locale, key));
        assertEquals("value_catalog_cache", catalogHandler.getAll().get("en").get(key));
    }

    private static Map<Object, Object> cacheToArray(Cache cache) {
        var nativeCache = (ConcurrentHashMap<?, ?>) cache.getNativeCache();
        return new HashMap<>(nativeCache);
    }
}
