package io.github.alaugks.spring.messagesource.base.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class CatalogTest {

    static Catalog catalog;

    @BeforeEach
    void BeforeEach() {

        List<TransUnit> transUnits = new ArrayList<>();

        // Domain foo
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "key_1", "value_en_1", "foo"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "key_2", "value_en_2", "foo"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "key_1", "value_en_3", "foo")); // Check overwrite
        // Domain bar
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "key_1", "value_en_1", "bar"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "key_2", "value_en_2", "bar"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "key_1", "value_en_3", "bar")); // Check overwrite
        // Domain foo
        transUnits.add(new TransUnit(Locale.forLanguageTag("en-US"), "key_1", "value_en_us_1", "foo"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("en_US"), "key_2", "value_en_us_2", "foo"));

        catalog = new Catalog(transUnits, Locale.forLanguageTag("en"), "foo");
        catalog.build();
    }

    @Test
    void test_fallback() {
        // Domain foo
        Locale locale = Locale.forLanguageTag("en");
        assertEquals("value_en_1", catalog.get(locale, "foo.key_1"));
        assertEquals("value_en_1", catalog.get(locale, "key_1"));
    }

    @Test
    void test_en() {
        // Domain foo
        Locale locale = Locale.forLanguageTag("en");
        assertEquals("value_en_1", catalog.get(locale, "foo.key_1"));
        // Domain bar
        assertEquals("value_en_1", catalog.get(locale, "bar.key_1"));
        // Domain foo
        assertEquals("value_en_2", catalog.get(locale, "foo.key_2"));
        // Domain bar
        assertEquals("value_en_2", catalog.get(locale, "bar.key_2"));

        // Domain bar
        assertNull(catalog.get(locale, "bar.key_3"));
        // Domain foo
        assertNull(catalog.get(locale, "foo.key_3"));
    }

    @Test
    void test_enUk_withDash() {
        Locale locale = Locale.forLanguageTag("en-US");
        // Domain foo
        assertEquals("value_en_us_1", catalog.get(locale, "foo.key_1"));
    }

    @Test
    void test_get_paramValuesEmpty() {
        assertNull(catalog.get(Locale.forLanguageTag("en"), ""));
        assertNull(catalog.get(Locale.forLanguageTag(""), "messages.m_en_1"));
    }

}
