package io.github.alaugks.spring.messagesource.base.catalog;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.springframework.util.Assert;

public final class CatalogHandler implements CatalogHandlerInterface {

    private final CatalogInterface catalog;

    private CatalogHandler(CatalogInterface catalog) {
        this.catalog = catalog;
    }

    public static Builder builder(CatalogInterface catalog) {
        return new Builder(catalog);
    }

    public static final class Builder {

        private final CatalogInterface baseCatalog;
        List<CatalogInterface> catalogList = new ArrayList<>();

        public Builder(CatalogInterface baseCatalog) {
            Assert.notNull(baseCatalog, "Argument baseCatalog must not be null");
            this.baseCatalog = baseCatalog;
        }

        public Builder catalogCache(CatalogInterface cacheCatalog) {
            Assert.notNull(cacheCatalog, "Argument cacheCatalog must not be null");
            this.catalogList.add(0, cacheCatalog);
            return this;
        }

        public Builder addCatalog(CatalogInterface catalog) {
            Assert.notNull(catalog, "Argument catalog must not be null");
            this.catalogList.add(catalog);
            return this;
        }

        public CatalogHandler build() {
            this.catalogList.add(this.baseCatalog);

            Iterator<CatalogInterface> iterator = this.catalogList.iterator();
            CatalogInterface current;
            CatalogInterface next;
            CatalogInterface catalog = iterator.next();

            if (iterator.hasNext()) {
                current = catalog;
                while (iterator.hasNext()) {
                    next = iterator.next();
                    current.nextHandler(next);
                    current = next;
                }
            }

            catalog.build();
            return new CatalogHandler(catalog);
        }
    }

    @Override
    public Map<String, Map<String, String>> getAll() {
        return this.catalog.getAll();
    }

    @Override
    public String get(Locale locale, String code) {
        return this.catalog.get(locale, code);
    }
}
