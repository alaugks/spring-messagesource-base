package io.github.alaugks.spring.messagesource.base.records;

import io.github.alaugks.spring.messagesource.base.BaseTranslationMessageSource;
import java.util.Locale;

public record Translation(Locale locale, String code, String value, String domain) {
    public Translation(Locale locale, String code, String value) {
        this(locale, code, value, BaseTranslationMessageSource.DEFAULT_DOMAIN);
    }
}
