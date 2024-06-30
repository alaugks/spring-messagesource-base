package io.github.alaugks.spring.messagesource.catalog.catalog;

import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Locale.Builder;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.util.Assert;

public final class Catalog extends CatalogAbstract {

    public static final String DEFAULT_DOMAIN = "messages";
    private final Map<Locale, Map<String, String>> catalogMap;
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

        this.catalogMap = new ConcurrentHashMap<>();
        this.transUnits = transUnits;
        this.defaultLocale = defaultLocale;
        this.defaultDomain = defaultDomain;
    }

    @Override
    public String resolveCode(Locale locale, String code) {
        if (locale.toString().isEmpty() || code.isEmpty()) {
            return super.resolveCode(locale, code);
        }

        return this.resolveFromCatalogMap(locale, code).orElse(null);
    }

    @Override
    public Map<Locale, Map<String, String>> getAll() {
        return this.catalogMap.isEmpty() ? super.getAll() : this.catalogMap;
    }

    @Override
    public void build() {
        super.build();
        this.transUnits.forEach(t -> this.put(t.locale(), t.code(), t.value(), t.domain()));
    }

    private void put(Locale locale, String code, String value, String domain) {
        if (locale.toString().isEmpty() || code.isEmpty()) {
            return;
        }
        this.catalogMap.putIfAbsent(locale, new HashMap<>());
        this.catalogMap.get(locale).putIfAbsent(concatCode(domain, code), value);
    }

    private Optional<String> resolveFromCatalogMap(Locale locale, String code) {
        return this.getTargetValue(locale, code)
            .or(() -> this.getTargetValue(locale, concatCode(this.defaultDomain, code)))
            .or(() -> this.getTargetValue(buildLocaleWithoutRegion(locale), code))
            .or(() -> this.getTargetValue(buildLocaleWithoutRegion(locale), concatCode(this.defaultDomain, code)))
            .or(() -> this.getTargetValue(this.defaultLocale, code))
            .or(() -> this.getTargetValue(this.defaultLocale, concatCode(this.defaultDomain, code)));
    }

    private Optional<String> getTargetValue(Locale locale, String code) {
        return Optional.ofNullable(this.catalogMap.get(locale)).flatMap(
            languageCatalog -> Optional.ofNullable(languageCatalog.get(code))
        );
    }

    private static String concatCode(String domain, String code) {
        return Optional.ofNullable(domain).orElse(DEFAULT_DOMAIN) + "." + code;
    }

    private static Locale buildLocaleWithoutRegion(Locale locale) {
        return new Builder().setLanguage(locale.getLanguage()).build();
    }
}
