package io.github.alaugks.spring.messagesource.base;

import io.github.alaugks.spring.messagesource.base.catalog.CatalogInterface;
import io.github.alaugks.spring.messagesource.base.records.TransUnitCatalog;
import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import org.springframework.context.support.AbstractMessageSource;
import org.springframework.util.Assert;

public class CatalogMessageSource extends AbstractMessageSource {

    public static final String DEFAULT_DOMAIN = "messages";

    private final CatalogInterface catalog;

    private CatalogMessageSource(CatalogInterface catalog) {
        this.catalog = catalog;
    }

    public static Builder builder(CatalogInterface catalog) {
        return new Builder(catalog);
    }

    public static final class Builder {

        private CatalogInterface catalog;
        private CatalogInterface catalogCache;

        public Builder(CatalogInterface catalog) {
            Assert.notNull(catalog, "Argument catalog must not be null");
            this.catalog = catalog;
        }

        public Builder catalogCache(CatalogInterface cacheCatalog) {
            Assert.notNull(cacheCatalog, "Argument cacheCatalog must not be null");
            this.catalogCache = cacheCatalog;
            return this;
        }

        public CatalogMessageSource build() {
            if (catalogCache != null) {
                this.catalog = catalogCache.nextHandler(this.catalog);
            }

            catalog.build();
            return new CatalogMessageSource(this.catalog);
        }
    }

    public List<TransUnitCatalog> getAll() {
        return this.catalog.getAll();
    }

    public String get(Locale locale, String code) {
        return this.catalog.resolve(locale, code);
    }

    @Override
    protected MessageFormat resolveCode(String code, Locale locale) {
        try {
            return new MessageFormat(this.get(locale, code), locale);
        } catch (Exception e) {
            return null;
        }
    }
}
