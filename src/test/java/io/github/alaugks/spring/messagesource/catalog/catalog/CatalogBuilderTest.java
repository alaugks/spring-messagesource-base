package io.github.alaugks.spring.messagesource.catalog.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.alaugks.spring.messagesource.catalog.CatalogMessageSource;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.cache.Cache;
import org.springframework.cache.concurrent.ConcurrentMapCache;

class CatalogBuilderTest {

    private final Locale locale = Locale.forLanguageTag("en");

    @Test
    void test_withoutCache() {
        String domain = "messages";
        String key = domain + ".key";
        List<TransUnit> transUnits = List.of(
            new TransUnit(this.locale, "key", "from_base_catalog")
        );

        var messageSource = new CatalogMessageSource(
            CatalogBuilder.builder(transUnits, this.locale).build()
        );

        assertEquals("from_base_catalog", messageSource.getMessage(key, null, this.locale));
    }

    @Test
    void test_withCache() {
        String domain = "messages";
        String key = domain + ".key";
        String localeKey = "en|" + key;
        List<TransUnit> transUnits = List.of(
            new TransUnit(this.locale, "key", "from_base_catalog")
        );

        var cache = new ConcurrentMapCache("text-cache");
        var cacheCatalog = new CatalogCache(cache);

        // Is translation in cache?
        var cacheBuffer = cacheToArray(cache);
        assertNull(cacheBuffer.get(localeKey));

        var messageSource = new CatalogMessageSource(
            CatalogBuilder.builder(transUnits, this.locale).catalogCache(cacheCatalog).build()
        );

        // Exists item in Cache after build?
        assertEquals("from_base_catalog", cacheToArray(cache).get(localeKey));

        // Hit
        assertEquals("from_base_catalog", messageSource.getMessage(key, null, this.locale));

        // Remote item from Cache
        cache.evictIfPresent(localeKey);

        // Is item removed from cache?
        assertNull(cacheToArray(cache).get(localeKey));

        // Get from Catalog and put to Cache
        assertEquals("from_base_catalog", messageSource.getMessage(key, null, this.locale));
        assertEquals("from_base_catalog", cacheToArray(cache).get(localeKey));

        // CatalogCache Hit
        // Overwrite cacheItem to test translation is from Cache
        cache.put(localeKey, "value_catalog_cache");
        assertEquals("value_catalog_cache", messageSource.getMessage(key, null, this.locale));
    }

    @Test
    void test_withoutSetDefaultDomain() {
        List<TransUnit> transUnits = Arrays.asList(
            new TransUnit(this.locale, "key", "messages_value"),
            new TransUnit(this.locale, "key", "foo_value", "foo")
        );

        var messageSource = new CatalogMessageSource(
            CatalogBuilder.builder(transUnits, this.locale).build()
        );

        assertEquals("messages_value", messageSource.getMessage("key", null, this.locale));
    }

    @Test
    void test_withSetDefaultDomain() {
        List<TransUnit> transUnits = Arrays.asList(
            new TransUnit(this.locale, "key", "messages_value"),
            new TransUnit(this.locale, "key", "foo_value", "foo")
        );

        var messageSource = new CatalogMessageSource(
            CatalogBuilder.builder(transUnits, this.locale).defaultDomain("foo").build()
        );

        assertEquals("foo_value", messageSource.getMessage("key", null, this.locale));
    }

    private static Map<Object, Object> cacheToArray(Cache cache) {
        var nativeCache = (ConcurrentHashMap<?, ?>) cache.getNativeCache();
        return new HashMap<>(nativeCache);
    }
}
