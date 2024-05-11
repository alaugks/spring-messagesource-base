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

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {

        List<CatalogInterface> catalogList = new ArrayList<>();

        public Builder addHandler(CatalogInterface catalog) {
            Assert.notNull(catalog, "cache must not be null");
            this.catalogList.add(catalog);
            return this;
        }

        public CatalogHandler build() {
            Assert.isTrue(!this.catalogList.isEmpty(), "cache must not be null");

            Iterator<CatalogInterface> list = this.catalogList.iterator();
            CatalogInterface catalog = list.next();
            while (list.hasNext()) {
                CatalogInterface nextCatalog = list.next();
                if (nextCatalog != null) {
                    catalog.nextHandler(nextCatalog);
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
