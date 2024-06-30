package io.github.alaugks.spring.messagesource.catalog;

import io.github.alaugks.spring.messagesource.catalog.catalog.Catalog;
import io.github.alaugks.spring.messagesource.catalog.catalog.CatalogCache;
import io.github.alaugks.spring.messagesource.catalog.catalog.CatalogInterface;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.Assert;

public class CatalogMessageSource extends AbstractMessageSource {

    private final CatalogInterface catalog;

    private CatalogMessageSource(CatalogInterface catalog) {
        this.catalog = catalog;
    }

    public static Builder builder(List<TransUnit> transUnits, Locale defaultLocale) {
        return new Builder(transUnits, defaultLocale);
    }

    public static final class Builder {

        private final Locale defaultLocale;
        private final List<TransUnit> transUnits;
        private String defaultDomain = Catalog.DEFAULT_DOMAIN;

        public Builder(List<TransUnit> transUnits, Locale defaultLocale) {
            Assert.notNull(transUnits, "Argument defaultLocale must not be null");
            Assert.notNull(defaultLocale, "Argument defaultLocale must not be null");
            this.transUnits = transUnits;
            this.defaultLocale = defaultLocale;
        }

        public Builder defaultDomain(String defaultDomain) {
            Assert.notNull(defaultDomain, "Argument defaultDomain must not be null");
            this.defaultDomain = defaultDomain;
            return this;
        }

        public CatalogMessageSource build() {
            CatalogInterface catalogCache = new CatalogCache().nextHandler(
                new Catalog(this.transUnits, this.defaultLocale, this.defaultDomain)
            );
            catalogCache.build();
            return new CatalogMessageSource(catalogCache);
        }
    }

    public Map<Locale, Map<String, String>> getAll() {
        return this.catalog.getAll();
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        String value = this.catalog.resolveCode(locale, code);
        if (value != null) {
            return new MessageFormat(value, locale);
        }
        return null;
    }
}
