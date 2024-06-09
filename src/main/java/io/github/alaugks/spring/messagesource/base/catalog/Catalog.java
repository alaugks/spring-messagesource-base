package io.github.alaugks.spring.messagesource.base.catalog;

import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import io.github.alaugks.spring.messagesource.base.records.TransUnitCatalog;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import org.springframework.util.Assert;

public final class Catalog extends CatalogAbstract {

    public static final String DEFAULT_DOMAIN = "messages";
    private final HashMap<String, Map<String, String>> catalogMap;
    private final Locale defaultLocale;
    private final String defaultDomain;
    private final List<TransUnit> transUnits;

    public Catalog(List<TransUnit> transUnits, Locale defaultLocale) {
        this(transUnits, defaultLocale, DEFAULT_DOMAIN);
    }

    public Catalog(List<TransUnit> transUnits, Locale defaultLocale, String defaultDomain) {
        Assert.notNull(transUnits, "Argument transUnits must not be null");
        Assert.notNull(defaultLocale, "Argument defaultLocale must not be null");
        Assert.notNull(defaultDomain, "Argument defaultDomain must not be null");

        this.catalogMap = new HashMap<>();
        this.transUnits = transUnits;
        this.defaultLocale = defaultLocale;
        this.defaultDomain = defaultDomain;
    }

    @Override
    public List<TransUnitCatalog> getAll() {
        if (!this.catalogMap.isEmpty()) {
            List<TransUnitCatalog> transUnitCatalogs = new ArrayList<>();
            this.catalogMap.forEach((localeCode, transUnit) -> transUnit.forEach((code, value) ->
                transUnitCatalogs.add(
                    new TransUnitCatalog(
                        Locale.forLanguageTag(localeCode),
                        code,
                        value
                    )
                )
            ));
            return transUnitCatalogs;
        }
        return super.getAll();
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
    public void build() {
        super.build();
        this.transUnits.forEach(t -> this.put(t.locale(), t.code(), t.value(), t.domain()));
    }

    private void put(Locale locale, String code, String value, String domain) {
        if (!locale.toString().isEmpty() && !code.isEmpty()) {
            if (domain == null) {
                domain = DEFAULT_DOMAIN;
            }

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

    private String resolveCode(Locale locale, String code) {
        String value;

        // Code+LocaleRegion
        value = this.resolveCodeInCatalogMap(locale, code);
        if (value != null) {
            return value;
        }

        // Code+LocaleRegion / DomainCode+LanguageRegion
        value = this.resolveCodeInCatalogMap(locale, concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        // Code+Language / DomainCode+Language
        value = this.resolveCodeInCatalogMap(buildLocaleWithoutRegion(locale), code);
        if (value != null) {
            return value;
        }

        // Code+Language / DomainCode+Language
        value = this.resolveCodeInCatalogMap(buildLocaleWithoutRegion(locale), concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        // Code+DefaultLanguageRegion / DomainCode+DefaultLanguageRegion
        value = this.resolveCodeInCatalogMap(this.defaultLocale, code);
        if (value != null) {
            return value;
        }

        // Code+DefaultLanguageRegion / DomainCode+DefaultLanguageRegion
        value = this.resolveCodeInCatalogMap(this.defaultLocale, concatCode(this.defaultDomain, code));
        if (value != null) {
            return value;
        }

        return value;
    }

    public String resolveCodeInCatalogMap(Locale locale, String code) {
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
