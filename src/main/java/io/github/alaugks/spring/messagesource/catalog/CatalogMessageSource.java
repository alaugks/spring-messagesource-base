package io.github.alaugks.spring.messagesource.catalog;

import io.github.alaugks.spring.messagesource.catalog.catalog.CatalogBuilder;
import java.text.MessageFormat;
import java.util.Locale;
import org.springframework.context.support.AbstractMessageSource;

public class CatalogMessageSource extends AbstractMessageSource {

    private final CatalogBuilder catalogBuilder;

    public CatalogMessageSource(CatalogBuilder catalogBuilder) {
        this.catalogBuilder = catalogBuilder;
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String value = this.catalogBuilder.resolveCode(locale, code);
        if (value != null) {
            return new MessageFormat(value, locale);
        }
        return null;
    }
}
