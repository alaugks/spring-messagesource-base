package io.github.alaugks.spring.messagesource.base;

import io.github.alaugks.spring.messagesource.base.catalog.CatalogBuilder;
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
        try {
            return new MessageFormat(this.catalogBuilder.resolveCode(locale, code), locale);
        } catch (Exception e) {
            return null;
        }
    }
}
