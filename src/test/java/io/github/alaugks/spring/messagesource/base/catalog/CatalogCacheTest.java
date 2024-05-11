package io.github.alaugks.spring.messagesource.base.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.mock;

import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.cache.concurrent.ConcurrentMapCache;

@SuppressWarnings("java:S5778")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatalogCacheTest {

    private CatalogCache catalogCache;

    @BeforeAll
    void beforeAll() {
        var cache = new ConcurrentMapCache("text-cache");
        cache.put("en|messages.m_en_1", "value_m_en_1");
        cache.put("en|messages.m_en_2", "value_m_en_2");
        cache.put("en|domain.d_en_1", "value_d_en_1");
        cache.put("de|messages.m_de_1", "value_m_de_1");
        cache.put("de|messages.m_de_2", "value_m_de_2");
        cache.put("de|domain.d_de_1", "value_d_de_1");

        this.catalogCache = new CatalogCache(mock(CatalogInterface.class), cache);
    }

    @Test
    void test_getAll() {
        var all = this.catalogCache.getAll();
        var transEn = all.get(Locale.forLanguageTag("en").toString());
        var transDe = all.get(Locale.forLanguageTag("de").toString());

        assertEquals("value_m_en_1", transEn.get("messages.m_en_1"));
        assertEquals("value_m_en_2", transEn.get("messages.m_en_2"));
        assertEquals("value_d_en_1", transEn.get("domain.d_en_1"));
        assertEquals("value_m_de_1", transDe.get("messages.m_de_1"));
        assertEquals("value_m_de_2", transDe.get("messages.m_de_2"));
        assertEquals("value_d_de_1", transDe.get("domain.d_de_1"));
    }

    @Test
    void test_get() {
        assertEquals("value_m_en_1", catalogCache.get(Locale.forLanguageTag("en"), "messages.m_en_1"));
    }

    @Test
    void test_get_notExists() {
        assertNull(catalogCache.get(Locale.forLanguageTag("en"), "messages.not_exists"));
    }

    @Test
    void test_get_paramValuesEmpty() {
        assertNull(catalogCache.get(Locale.forLanguageTag("en"), ""));
        assertNull(catalogCache.get(Locale.forLanguageTag(""), "messages.m_en_1"));
    }

    @Test
    void test_cache_paramter_isNull() {
        try {
            new CatalogCache(mock(CatalogInterface.class), null);
            fail("Exception should be throw");
        } catch (IllegalArgumentException e) {
            assertInstanceOf(IllegalArgumentException.class, e);
        }
    }
}
