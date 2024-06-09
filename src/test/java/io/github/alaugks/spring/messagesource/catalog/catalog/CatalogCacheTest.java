package io.github.alaugks.spring.messagesource.catalog.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnitCatalog;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.cache.concurrent.ConcurrentMapCache;

@SuppressWarnings("java:S5778")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CatalogCacheTest {

    private CatalogCache catalogCache;

    @BeforeAll
    void beforeAll() {
        var cache = new ConcurrentMapCache("test-cache");
        cache.put("en|messages.m_en_1", "value_m_en_1");
        cache.put("en|messages.m_en_2", "value_m_en_2");
        cache.put("en|domain.d_en_1", "value_d_en_1");
        cache.put("de|messages.m_de_1", "value_m_de_1");
        cache.put("de|messages.m_de_2", "value_m_de_2");
        cache.put("de|domain.d_de_1", "value_d_de_1");

        this.catalogCache = new CatalogCache(cache);
    }

    @Test
    void test_get() {
        assertEquals("value_m_en_1", catalogCache.resolve(Locale.forLanguageTag("en"), "messages.m_en_1"));
    }

    @Test
    void test_get_notExists() {
        assertNull(catalogCache.resolve(Locale.forLanguageTag("en"), "messages.not_exists"));
    }

    @Test
    void test_get_paramValuesEmpty() {
        assertNull(catalogCache.resolve(Locale.forLanguageTag("en"), ""));
        assertNull(catalogCache.resolve(Locale.forLanguageTag(""), "messages.m_en_1"));
    }

    @Test
    void test_cache_paramter_isNull() {
        try {
            new CatalogCache(null);
            fail("Exception should be throw");
        } catch (IllegalArgumentException e) {
            assertInstanceOf(IllegalArgumentException.class, e);
        }
    }

    @ParameterizedTest()
    @MethodSource("dataProvider_getAllCatalogTransUnits")
    void test_getAllCatalogTransUnits(String locale, String code, String expected) {
        for (TransUnitCatalog translation : this.catalogCache.getAll()) {
            if (translation.locale().toString().equals(locale) && translation.code().equals(code)) {
                assertEquals(expected, translation.value());
                return;
            }
        }
        fail(String.format("Expected failed on %s", code));
    }

    private static Stream<Arguments> dataProvider_getAllCatalogTransUnits() {
        return Stream.of(
            Arguments.of("en", "messages.m_en_1", "value_m_en_1"),
            Arguments.of("en", "messages.m_en_2", "value_m_en_2"),
            Arguments.of("en", "domain.d_en_1", "value_d_en_1"),
            Arguments.of("de", "messages.m_de_1", "value_m_de_1"),
            Arguments.of("de", "messages.m_de_2", "value_m_de_2"),
            Arguments.of("de", "domain.d_de_1", "value_d_de_1")
        );
    }
}
