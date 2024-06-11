package io.github.alaugks.spring.messagesource.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alaugks.spring.messagesource.catalog.catalog.CatalogBuilder;
import io.github.alaugks.spring.messagesource.catalog.catalog.CatalogCache;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.context.MessageSource;

class CatalogMessageSourceExampleTest {

    MessageSource messageSource;

    public CatalogMessageSourceExampleTest() {
        this.messageSource = this.messageSource();
    }

    public MessageSource messageSource() {

        List<TransUnit> transUnits = new ArrayList<>();

        var localeEn = Locale.forLanguageTag("en");
        transUnits.add(new TransUnit(localeEn, "headline", "Headline"));
        transUnits.add(new TransUnit(localeEn, "postcode", "Postcode"));
        transUnits.add(new TransUnit(localeEn, "validation.email.exists", "Your email {0} has been registered."));
        transUnits.add(new TransUnit(localeEn, "default-message", "This is a default message."));
        transUnits.add(new TransUnit(localeEn, "headline", "Payment", "payment"));
        transUnits.add(new TransUnit(localeEn, "form.expiry_date", "Expiry date", "payment"));

        var localeEnUs = Locale.forLanguageTag("en-US");
        transUnits.add(new TransUnit(localeEnUs, "postcode", "Zip code"));
        transUnits.add(new TransUnit(localeEnUs, "form.expiry_date", "Expiration date", "payment"));

        var localeDe = Locale.forLanguageTag("de");
        transUnits.add(new TransUnit(localeDe, "headline", "Überschrift"));
        transUnits.add(new TransUnit(localeDe, "postcode", "Postleitzahl"));
        transUnits.add(new TransUnit(localeDe, "validation.email.exists", "Ihre E-Mail {0} wurde registriert."));
        transUnits.add(new TransUnit(localeDe, "default-message", "Das ist ein Standardtext."));
        transUnits.add(new TransUnit(localeDe, "headline", "Zahlung", "payment"));
        transUnits.add(new TransUnit(localeDe, "form.expiry_date", "Ablaufdatum", "payment"));

        return new CatalogMessageSource(
            CatalogBuilder
                .builder(transUnits, Locale.forLanguageTag("en"))
                .catalogCache(new CatalogCache(new ConcurrentMapCache("my-cache")))
                .build()
        );
    }


    @ParameterizedTest()
    @MethodSource("dataProvider_examples")
    void test_example(String code, String locale, Object expected, Object[] args) {
        String message = this.messageSource.getMessage(
            code,
            args,
            Locale.forLanguageTag(locale)
        );
        assertEquals(expected, message);
    }

    private static Stream<Arguments> dataProvider_examples() {
        return Stream.of(
            Arguments.of("headline", "en", "Headline", null),
            Arguments.of("messages.headline", "en", "Headline", null),
            Arguments.of("postcode", "en", "Postcode", null),
            Arguments.of("messages.postcode", "en", "Postcode", null),
            Arguments.of("validation.email.exists", "en", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.validation.email.exists", "en", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "en", "This is a default message.", null),
            Arguments.of("messages.default-message", "en", "This is a default message.", null),
            Arguments.of("payment.headline", "en", "Payment", null),
            Arguments.of("payment.form.expiry_date", "en", "Expiry date", null),

            Arguments.of("headline", "en-US", "Headline", null),
            Arguments.of("messages.headline", "en-US", "Headline", null),
            Arguments.of("postcode", "en-US", "Zip code", null),
            Arguments.of("messages.postcode", "en-US", "Zip code", null),
            Arguments.of("validation.email.exists", "en-US", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.validation.email.exists", "en-US", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "en-US", "This is a default message.", null),
            Arguments.of("messages.default-message", "en-US", "This is a default message.", null),
            Arguments.of("payment.headline", "en-US", "Payment", null),
            Arguments.of("payment.form.expiry_date", "en-US", "Expiration date", null),

            Arguments.of("headline", "de", "Überschrift", null),
            Arguments.of("messages.headline", "de", "Überschrift", null),
            Arguments.of("postcode", "de", "Postleitzahl", null),
            Arguments.of("messages.postcode", "de", "Postleitzahl", null),
            Arguments.of("validation.email.exists", "de", "Ihre E-Mail foo@example.com wurde registriert.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.validation.email.exists", "de", "Ihre E-Mail foo@example.com wurde registriert.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "de", "Das ist ein Standardtext.", null),
            Arguments.of("messages.default-message", "de", "Das ist ein Standardtext.", null),
            Arguments.of("payment.headline", "de", "Zahlung", null),
            Arguments.of("payment.form.expiry_date", "de", "Ablaufdatum", null),

            Arguments.of("headline", "jp", "Headline", null),
            Arguments.of("messages.headline", "jp", "Headline", null),
            Arguments.of("postcode", "jp", "Postcode", null),
            Arguments.of("messages.postcode", "jp", "Postcode", null),
            Arguments.of("validation.email.exists", "jp", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.validation.email.exists", "jp", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "jp", "This is a default message.", null),
            Arguments.of("messages.default-message", "jp", "This is a default message.", null),
            Arguments.of("payment.headline", "jp", "Payment", null),
            Arguments.of("payment.form.expiry_date", "jp", "Expiry date", null)
        );
    }
}
