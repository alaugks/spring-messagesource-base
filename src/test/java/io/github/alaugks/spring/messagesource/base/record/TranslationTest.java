package io.github.alaugks.spring.messagesource.base.record;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alaugks.spring.messagesource.base.records.Translation;
import java.util.Locale;
import org.junit.jupiter.api.Test;

class TranslationTest {

    @Test
    void test_withoutDomain() {
        var records = new Translation(Locale.forLanguageTag("en"), "the-code", "the-value");

        assertEquals(Locale.forLanguageTag("en"), records.locale());
        assertEquals("the-code", records.code());
        assertEquals("the-value", records.value());
        assertEquals("messages", records.domain());
    }

    @Test
    void test_witDomain() {
        var records = new Translation(Locale.forLanguageTag("en"), "the-code", "the-value", "my-domain");

        assertEquals(Locale.forLanguageTag("en"), records.locale());
        assertEquals("the-code", records.code());
        assertEquals("the-value", records.value());
        assertEquals("my-domain", records.domain());
    }
}
