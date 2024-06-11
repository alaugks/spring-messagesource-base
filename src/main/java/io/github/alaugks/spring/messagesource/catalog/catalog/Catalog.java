package io.github.alaugks.spring.messagesource.catalog.catalog;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;

public final class Catalog extends CatalogAbstract {

    public static final String DEFAULT_DOMAIN = "messages";
    private final Map<Locale, Map<String, String>> catalogMap;
    private final Locale defaultLocale;
    private final String defaultDomain;
    private final List<TransUnit> transUnits;

    public Catalog(List<TransUnit> transUnits, Locale defaultLocale, String defaultDomain) {
        Assert.notNull(transUnits, "Argument transUnits must not be null");
        Assert.notNull(defaultLocale, "Argument defaultLocale must not be null");
        Assert.notNull(defaultDomain, "Argument defaultDomain must not be null");

        this.catalogMap = new ConcurrentHashMap<>();
        this.transUnits = transUnits;
        this.defaultLocale = defaultLocale;
        this.defaultDomain = defaultDomain;
    }

    @Override
    public String resolve(Locale locale, String code) {

        if (locale.toString().isEmpty() || code.isEmpty()) {
            return null;
        }

        String value = this.resolveCode(locale, code);
        if (value != null) {
            return value;
        }

        return super.resolve(locale, code);
    }

    @Override
    public Map<Locale, Map<String, String>> getAll() {
        if (!this.catalogMap.isEmpty()) {
            return this.catalogMap;
        }

        return super.getAll();
    }

    @Override
    public void build() {
        super.build();
        this.transUnits.forEach(t -> this.put(t.locale(), t.code(), t.value(), t.domain()));
    }

    private void put(Locale locale, String code, String value, String domain) {
        if (!locale.toString().isEmpty() && !code.isEmpty()) {
            if (domain == null) {
                domain = DEFAULT_DOMAIN;
            }

            this.catalogMap.putIfAbsent(
                locale,
                new HashMap<>()
            );

            this.catalogMap.get(locale).putIfAbsent(
                concatCode(domain, code),
                value
            );
        }
    }

    private String resolveCode(Locale locale, String code) {
        String value;

        // locale and code
        value = this.resolveCodeInCatalogMap(locale, code);
        if (value != null) {
            return value;
        }

        // locale AND domain.code
        value = this.resolveCodeInCatalogMap(locale, concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        // locale(without region) code
        value = this.resolveCodeInCatalogMap(buildLocaleWithoutRegion(locale), code);
        if (value != null) {
            return value;
        }

        // locale(without region) domain.code
        value = this.resolveCodeInCatalogMap(buildLocaleWithoutRegion(locale), concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        // default locale AND code
        value = this.resolveCodeInCatalogMap(this.defaultLocale, code);
        if (value != null) {
            return value;
        }

        // default locale AND domain.code
        value = this.resolveCodeInCatalogMap(this.defaultLocale, concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        return value;
    }

    private String resolveCodeInCatalogMap(Locale locale, String code) {
        if (this.catalogMap.containsKey(locale)) {
            Map<String, String> languageCatalog = this.catalogMap.get(locale);
            if (languageCatalog.containsKey(code)) {
                return languageCatalog.get(code);
            }
        }
        return null;
    }

    private static String concatCode(String domain, String code) {
        return domain + "." + code;
    }

    private static Locale buildLocaleWithoutRegion(Locale locale) {
        Builder localeBuilder = new Builder();
        localeBuilder.setLanguage(locale.getLanguage());
        return localeBuilder.build();
    }
}
