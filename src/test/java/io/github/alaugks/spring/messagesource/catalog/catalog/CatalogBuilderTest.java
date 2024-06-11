package io.github.alaugks.spring.messagesource.catalog.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import io.github.alaugks.spring.messagesource.catalog.CatalogMessageSource;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import org.junit.jupiter.api.Test;
import org.springframework.context.NoSuchMessageException;

class CatalogBuilderTest {

    private final Locale locale = Locale.forLanguageTag("en");

    @Test
    void test_putCache() {
        List<TransUnit> transUnits = List.of(
            new TransUnit(this.locale, "key", "messages_value")
        );

        CatalogBuilder catalogBuilder = CatalogBuilder.builder(transUnits, this.locale).build();
        var messageSource = new CatalogMessageSource(catalogBuilder);

        try {
            messageSource.getMessage("messages.key", null, this.locale);
            messageSource.getMessage("key", null, this.locale);
            messageSource.getMessage("not-exists", null, this.locale);
        } catch (NoSuchMessageException e) {
            //
        }

        catalogBuilder.getAll();
        assertEquals("messages_value", catalogBuilder.getAll().get(locale).get("key"));
        assertTrue(catalogBuilder.getAll().get(locale).containsKey("not-exists"));
        assertNull(catalogBuilder.getAll().get(locale).get("not-exists"));
    }

    @Test
    void test_getAll_emptyCatalog() {
        CatalogBuilder catalogBuilder = CatalogBuilder.builder(List.of(), this.locale).build();
        assertEquals(new ConcurrentHashMap<>(), catalogBuilder.getAll());
    }

    @Test
    void test_getAll_ArgsNull() {
        CatalogBuilder catalogBuilder = CatalogBuilder.builder(List.of(), this.locale).build();
        assertNull(catalogBuilder.resolveCode(Locale.forLanguageTag(""), null));
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
}
