package io.github.alaugks.spring.messagesource.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.github.alaugks.spring.messagesource.base.catalog.Catalog;
import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
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
        transUnits.add(new TransUnit(localeEn, "email-notice", "Your email {0} has been registered."));
        transUnits.add(new TransUnit(localeEn, "default-message", "This is a default message."));
        transUnits.add(new TransUnit(localeEn, "headline", "Payment", "payment"));
        transUnits.add(new TransUnit(localeEn, "expiry_date", "Expiry date", "payment"));

        var localeEnUs = Locale.forLanguageTag("en-US");
        transUnits.add(new TransUnit(localeEnUs, "postcode", "Zip code"));
        transUnits.add(new TransUnit(localeEnUs, "expiry_date", "Expiration date", "payment"));

        var localeDe = Locale.forLanguageTag("de");
        transUnits.add(new TransUnit(localeDe, "headline", "Überschrift"));
        transUnits.add(new TransUnit(localeDe, "postcode", "Postleitzahl"));
        transUnits.add(new TransUnit(localeDe, "email-notice", "Ihre E-Mail {0} wurde registriert."));
        transUnits.add(new TransUnit(localeDe, "default-message", "Das ist ein Standardtext."));
        transUnits.add(new TransUnit(localeDe, "headline", "Zahlung", "payment"));
        transUnits.add(new TransUnit(localeDe, "expiry_date", "Ablaufdatum", "payment"));

        return CatalogMessageSource.builder(new Catalog(transUnits, Locale.forLanguageTag("en"))).build();
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
            Arguments.of("email-notice", "en", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.email-notice", "en", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "en", "This is a default message.", null),
            Arguments.of("messages.default-message", "en", "This is a default message.", null),
            Arguments.of("payment.headline", "en", "Payment", null),
            Arguments.of("payment.expiry_date", "en", "Expiry date", null),

            Arguments.of("headline", "en-US", "Headline", null),
            Arguments.of("messages.headline", "en-US", "Headline", null),
            Arguments.of("postcode", "en-US", "Zip code", null),
            Arguments.of("messages.postcode", "en-US", "Zip code", null),
            Arguments.of("email-notice", "en-US", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.email-notice", "en-US", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "en-US", "This is a default message.", null),
            Arguments.of("messages.default-message", "en-US", "This is a default message.", null),
            Arguments.of("payment.headline", "en-US", "Payment", null),
            Arguments.of("payment.expiry_date", "en-US", "Expiration date", null),

            Arguments.of("headline", "de", "Überschrift", null),
            Arguments.of("messages.headline", "de", "Überschrift", null),
            Arguments.of("postcode", "de", "Postleitzahl", null),
            Arguments.of("messages.postcode", "de", "Postleitzahl", null),
            Arguments.of("email-notice", "de", "Ihre E-Mail foo@example.com wurde registriert.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.email-notice", "de", "Ihre E-Mail foo@example.com wurde registriert.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "de", "Das ist ein Standardtext.", null),
            Arguments.of("messages.default-message", "de", "Das ist ein Standardtext.", null),
            Arguments.of("payment.headline", "de", "Zahlung", null),
            Arguments.of("payment.expiry_date", "de", "Ablaufdatum", null),

            Arguments.of("headline", "jp", "Headline", null),
            Arguments.of("messages.headline", "jp", "Headline", null),
            Arguments.of("postcode", "jp", "Postcode", null),
            Arguments.of("messages.postcode", "jp", "Postcode", null),
            Arguments.of("email-notice", "jp", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("messages.email-notice", "jp", "Your email foo@example.com has been registered.",
                new Object[]{"foo@example.com"}),
            Arguments.of("default-message", "jp", "This is a default message.", null),
            Arguments.of("messages.default-message", "jp", "This is a default message.", null),
            Arguments.of("payment.headline", "jp", "Payment", null),
            Arguments.of("payment.expiry_date", "jp", "Expiry date", null)
        );
    }
}
