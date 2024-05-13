package io.github.alaugks.spring.messagesource.base.records;

import io.github.alaugks.spring.messagesource.base.BaseTranslationMessageSource;
import java.util.Locale;

public record TransUnit(Locale locale, String code, String value, String domain) {
    public TransUnit(Locale locale, String code, String value) {
        this(locale, code, value, BaseTranslationMessageSource.DEFAULT_DOMAIN);
    }
}
