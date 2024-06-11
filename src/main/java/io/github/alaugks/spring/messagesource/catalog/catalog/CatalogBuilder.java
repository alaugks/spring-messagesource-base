package io.github.alaugks.spring.messagesource.catalog.catalog;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import java.util.List;
import java.util.Locale;
import org.springframework.util.Assert;

public class CatalogBuilder {

    private final CatalogInterface catalog;

    private CatalogBuilder(CatalogInterface catalog) {
        this.catalog = catalog;
    }

    public static Builder builder(List<TransUnit> transUnits, Locale defaultLocale) {
        return new Builder(transUnits, defaultLocale);
    }

    public static final class Builder {

        private final Locale defaultLocale;
        private final List<TransUnit> transUnits;
        private CatalogInterface catalogCache;
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

        public Builder catalogCache(CatalogInterface catalogCache) {
            Assert.notNull(catalogCache, "Argument catalogCache must not be null");
            this.catalogCache = catalogCache;
            return this;
        }

        public CatalogBuilder build() {
            CatalogInterface catalog = new Catalog(this.transUnits, this.defaultLocale, this.defaultDomain);

            if (this.catalogCache != null) {
                this.catalogCache.nextHandler(catalog);
                this.catalogCache.build();
                return new CatalogBuilder(this.catalogCache);
            }

            catalog.build();
            return new CatalogBuilder(catalog);
        }
    }

    public String resolveCode(Locale locale, String code) {
        return this.catalog.resolve(locale, code);
    }
}
