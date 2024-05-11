package io.github.alaugks.spring.messagesource.base.catalog;

import io.github.alaugks.spring.messagesource.base.records.Translation;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import org.springframework.util.Assert;

public final class Catalog extends CatalogAbstract {

    private final HashMap<String, Map<String, String>> catalogMap;
    private final Locale defaultLocale;
    private final String defaultDomain;
    private final List<Translation> translations;

    public Catalog(List<Translation> translations, Locale defaultLocale, String defaultDomain) {
        Assert.notNull(translations, "translations must not be null");
        Assert.notNull(defaultLocale, "defaultLocale must not be null");
        Assert.notNull(defaultDomain, "defaultDomain must not be null");

        this.catalogMap = new HashMap<>();
        this.translations = translations;
        this.defaultLocale = defaultLocale;
        this.defaultDomain = defaultDomain;

    }

    @Override
    public Map<String, Map<String, String>> getAll() {
        if (!this.catalogMap.isEmpty()) {
            return this.catalogMap;
        }
        return super.getAll();
    }

    @Override
    public String get(Locale locale, String code) {

        if (locale.toString().isEmpty() || code.isEmpty()) {
            return null;
        }

        String value = this.fromCatalogMap(locale, code);
        if (value != null) {
            return value;
        }

        return super.get(locale, code);
    }

    @Override
    public void build() {
        super.build();
        this.translations.forEach(t -> this.put(t.locale(), t.domain(), t.code(), t.value()));
    }

    private void put(Locale locale, String domain, String code, String value) {
        if (!locale.toString().isEmpty() && !code.isEmpty()) {
            String localeKey = super.localeToLocaleKey(locale);
            this.catalogMap.putIfAbsent(
                localeKey,
                new HashMap<>()
            );
            this.catalogMap.get(localeKey).putIfAbsent(
                concatCode(domain, code),
                value
            );
        }
    }

    private String fromCatalogMap(Locale locale, String code) {
        String value;

        // Code+LocaleRegion
        value = this.findInCatalogMap(locale, code);
        if (value != null) {
            return value;
        }

        // Code+LocaleRegion / DomainCode+LanguageRegion
        value = this.findInCatalogMap(locale, concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        // Code+Language / DomainCode+Language
        value = this.findInCatalogMap(buildLocaleWithoutRegion(locale), code);
        if (value != null) {
            return value;
        }

        // Code+Language / DomainCode+Language
        value = this.findInCatalogMap(buildLocaleWithoutRegion(locale), concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        // Code+DefaultLanguageRegion / DomainCode+DefaultLanguageRegion
        value = this.findInCatalogMap(this.defaultLocale, code);
        if (value != null) {
            return value;
        }

        // Code+DefaultLanguageRegion / DomainCode+DefaultLanguageRegion
        value = this.findInCatalogMap(this.defaultLocale, concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        return value;
    }

    public String findInCatalogMap(Locale locale, String code) {
        String localeKey = super.localeToLocaleKey(locale);
        if (this.catalogMap.containsKey(localeKey)) {
            Map<String, String> languageCatalog = this.catalogMap.get(localeKey);
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
