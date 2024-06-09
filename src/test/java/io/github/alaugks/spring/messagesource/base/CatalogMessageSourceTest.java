package io.github.alaugks.spring.messagesource.base;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import io.github.alaugks.spring.messagesource.base.catalog.Catalog;
import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;


/**
 * Order(100) -> getMessage(code, args, defaultMessage, locale)
 * Order(200) -> getMessage(code, args, locale)
 * Order(300) -> getMessage(resolvable, locale)
 * Order(400) -> getMessage(code, args, defaultMessage, locale) with other domain ("foo")
 */
@SuppressWarnings({"java:S4144"})
@TestMethodOrder(OrderAnnotation.class)
class CatalogMessageSourceTest {

    protected static MessageSource messageSource;

    @BeforeAll
    static void beforeAll() {
        List<TransUnit> transUnits = new ArrayList<>();
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "hello_world", "Hello World (messages / en)"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("de"), "hello_world", "Hallo Welt (messages / de)"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "roadrunner", "Road Runner and Wile E. Coyote"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("de"), "roadrunner", "Road Runner und Wile E. Coyote"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("en"), "bar", "Placeholder", "foo"));
        transUnits.add(new TransUnit(Locale.forLanguageTag("de"), "bar", "Platzhalter", "foo"));

        messageSource = CatalogMessageSource.builder(new Catalog(transUnits, Locale.forLanguageTag("en"))).build();
    }

    @Test
    @Order(100)
    void test_getMessage_Args_and_Default_messageExists() {
        assertEquals("Hello World (messages / en)", messageSource.getMessage(
            "hello_world",
            null,
            "My default message",
            Locale.forLanguageTag("en")
        ));

        assertEquals("Hallo Welt (messages / de)", messageSource.getMessage(
            "hello_world",
            null,
            "Meine Standardtext",
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(100)
    void test_getMessage_Args_and_Default_messageNotExists() {
        assertEquals("My default message", messageSource.getMessage(
            "not_exists",
            null,
            "My default message",
            Locale.forLanguageTag("en")
        ));

        assertEquals("Meine Standardtext", messageSource.getMessage(
            "not_exists",
            null,
            "Meine Standardtext",
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(100)
    void test_getMessage_Args_and_Default__messageNotExists_defaultIsNull() {
        assertNull(messageSource.getMessage(
            "not_exists",
            null,
            null,
            Locale.forLanguageTag("en")
        ));

        assertNull(messageSource.getMessage(
            "not_exists",
            null,
            null,
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(100)
    void test_getMessage_Args_and_Default_messageExists_messageWithArgs() {
        Object[] args = {"Road Runner", "Wile E. Coyote"};
        assertEquals("Road Runner and Wile E. Coyote", messageSource.getMessage(
            "roadrunner",
            args,
            "My default message",
            Locale.forLanguageTag("en")
        ));

        assertEquals("Road Runner und Wile E. Coyote", messageSource.getMessage(
            "roadrunner",
            args,
            "My default message",
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(100)
    void test_getMessage_Args_and_Default_messageNotExists_defaultMessageWithArgs() {
        Object[] args = {"Road Runner", "Wile E. Coyote"};
        assertEquals("Road Runner and Wile E. Coyote as default", messageSource.getMessage(
            "not_exists",
            args,
            "{0} and {1} as default",
            Locale.forLanguageTag("en")
        ));

        assertEquals("Road Runner and Wile E. Coyote as default", messageSource.getMessage(
            "not_exists",
            args,
            "{0} and {1} as default",
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(199)
    void test_getMessage_Args_and_Default_Nullable() {
        assertNull(messageSource.getMessage(
            "not_exists",
            null,
            null,
            Locale.forLanguageTag("en")
        ));

        assertNull(messageSource.getMessage(
            "not_exists",
            null,
            null,
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(200)
    void test_getMessage_Args_messageExists() {
        assertEquals("Hello World (messages / en)", messageSource.getMessage(
            "hello_world",
            null,
            Locale.forLanguageTag("en")
        ));

        assertEquals("Hallo Welt (messages / de)", messageSource.getMessage(
            "hello_world",
            null,
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(200)
    void test_getMessage_Args_messageExists_messageWithArgs() {
        Object[] args = {"Road Runner", "Wile E. Coyote"};

        assertEquals("Road Runner and Wile E. Coyote", messageSource.getMessage(
            "roadrunner",
            args,
            Locale.forLanguageTag("en")
        ));

        assertEquals("Road Runner und Wile E. Coyote", messageSource.getMessage(
            "roadrunner",
            args,
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(299)
    void test_getMessage_Args_NoSuchMessageException() {
        try {
            messageSource.getMessage(
                "not_exists",
                null,
                Locale.forLanguageTag("en")
            );
        } catch (NoSuchMessageException e) {
            assertEquals(NoSuchMessageException.class, e.getClass());
        }
    }

    @Test
    @Order(300)
    void test_getMessage_Resolvable_messageExists_messageWithArgs() {
        String[] codes = {"roadrunner"};
        Object[] args = {"Road Runner", "Wile E. Coyote"};

        assertEquals("Road Runner and Wile E. Coyote", messageSource.getMessage(
            new DefaultMessageSourceResolvable(
                codes,
                args
            ),
            Locale.forLanguageTag("en")
        ));

        assertEquals("Road Runner und Wile E. Coyote", messageSource.getMessage(
            new DefaultMessageSourceResolvable(
                codes,
                args
            ),
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(300)
    void test_getMessage_Resolvable_messageNotExists_withDefaultMessage() {
        String[] codes = {"not_exists"};

        assertEquals("This is a default message.", messageSource.getMessage(
            new DefaultMessageSourceResolvable(
                codes,
                null,
                "This is a default message."
            ),
            Locale.forLanguageTag("en")
        ));

        assertEquals("Das ist ein Standardtext.", messageSource.getMessage(
            new DefaultMessageSourceResolvable(
                codes,
                null,
                "Das ist ein Standardtext."
            ),
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    @Order(399)
    void test_getMessage_Resolvable_NoSuchMessageException() {
        Locale locale = Locale.forLanguageTag("en");
        String[] codes = {"not_exists"};
        DefaultMessageSourceResolvable resolvable = new DefaultMessageSourceResolvable(
            codes
        );

        assertThrows(NoSuchMessageException.class, () -> messageSource.getMessage(
            resolvable,
            locale
        ));
    }

    @Test
    @Order(400)
    void test_getMessage_withOtherDomain() {
        assertEquals("Placeholder", messageSource.getMessage(
            "foo.bar",
            null,
            "My default message",
            Locale.forLanguageTag("en")
        ));

        assertEquals("Platzhalter", messageSource.getMessage(
            "foo.bar",
            null,
            "Meine Standardtext",
            Locale.forLanguageTag("de")
        ));
    }

    @Test
    void test_messagesFormat_choice() {
        List<TransUnit> transUnits = new ArrayList<>();
        transUnits.add(
            new TransUnit(
                Locale.forLanguageTag("en"), "format_choice",
                "There {0,choice,0#are no files|1#is one file|1<are {0,number,integer} files}."
            )
        );
        transUnits.add(
            new TransUnit(
                Locale.forLanguageTag("de"), "format_choice",
                "Es gibt {0,choice,0#keine Datei|1#eine Datei|1<{0,number,integer} Dateien}."
            )
        );

        var messageSourceChoice = CatalogMessageSource
            .builder(new Catalog(transUnits, Locale.forLanguageTag("en")))
            .build();

        assertEquals("There are 10,000 files.", messageSourceChoice.getMessage(
            "format_choice",
            new Object[]{10000L},
            Locale.forLanguageTag("en")
        ));
        assertEquals("There is one file.", messageSourceChoice.getMessage(
            "format_choice",
            new Object[]{1},
            Locale.forLanguageTag("en")
        ));

        assertEquals("Es gibt 10.000 Dateien.", messageSourceChoice.getMessage(
            "format_choice",
            new Object[]{10000L},
            Locale.forLanguageTag("de")
        ));
        assertEquals("Es gibt eine Datei.", messageSourceChoice.getMessage(
            "format_choice",
            new Object[]{1},
            Locale.forLanguageTag("de")
        ));
    }

}
