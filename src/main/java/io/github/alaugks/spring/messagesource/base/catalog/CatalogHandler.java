package io.github.alaugks.spring.messagesource.base.catalog;

import java.util.Locale;
import java.util.Map;
import org.springframework.cache.Cache;
import org.springframework.util.Assert;

public final class CatalogHandler {

    private final CatalogInterface catalog;

    public CatalogHandler(CatalogInterface catalog) {
        this.catalog = catalog;
    }

    public static Builder builder(Catalog catalog) {
        return new Builder(catalog);
    }

    public static final class Builder {

        private final Catalog catalog;
        private Cache cache;

        public Builder(Catalog catalog) {
            Assert.notNull(catalog, "catalog must not be null");

            this.catalog = catalog;
        }

        public Builder withCache(Cache cache) {
            Assert.notNull(cache, "cache must not be null");

            this.cache = cache;
            return this;
        }

        public CatalogHandler build() {
            if (this.cache != null) {
                return new CatalogHandler(new CatalogCache(this.catalog, this.cache));
            }
            return new CatalogHandler(this.catalog);
        }
    }

    public Map<String, Map<String, String>> getAll() {
        return this.catalog.getAll();
    }

    public String get(Locale locale, String code) {
        return this.catalog.get(locale, code);
    }
}
