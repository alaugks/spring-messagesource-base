package io.github.alaugks.spring.messagesource.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alaugks.spring.messagesource.base.catalog.Catalog;
import io.github.alaugks.spring.messagesource.base.catalog.CatalogHandler;
import io.github.alaugks.spring.messagesource.base.records.Translation;
import io.github.alaugks.spring.messagesource.base.records.TranslationFile;
import io.github.alaugks.spring.messagesource.base.ressources.ResourcesLoader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;

class BaseTranslationMessageSourceCasesTest {

    MessageSource messageSource;

    public BaseTranslationMessageSourceCasesTest() throws IOException {
        List<Translation> translations = new ArrayList<>();
        var resourcesLoader = new ResourcesLoader(
            Locale.forLanguageTag("en"),
            new HashSet<>(List.of("translations_example/*")),
            List.of("json")
        );

        for (TranslationFile file : resourcesLoader.getTranslationFiles()) {
            String json = new String(file.inputStream().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> items = mapper.convertValue(mapper.readTree(json), new TypeReference<>() {});

            for (Map.Entry<String, Object> item : items.entrySet()) {
                translations.add(
                    new Translation(
                        file.locale(), item.getKey(), item.getValue().toString(), file.domain()
                    )
                );
            }
        }

        this.messageSource = new BaseTranslationMessageSource(
            CatalogHandler
                .builder()
                .addHandler(new Catalog(translations, Locale.forLanguageTag("en")))
                .build()
        );

    }

    @ParameterizedTest()
    @MethodSource("dataProvider_examples")
    void test_complex_locale_region_fallback(String code, String locale, Object expected) {
        String message = this.messageSource.getMessage(
            code,
            null,
            Locale.forLanguageTag(locale)
        );
        assertEquals(expected, message);
    }

    private static Stream<Arguments> dataProvider_examples() {
        return Stream.of(
            Arguments.of("headline", "en", "Headline (en)"),
            Arguments.of("messages.headline", "en", "Headline (en)"),
            Arguments.of("text", "en", "Text (en)"),
            Arguments.of("messages.text", "en", "Text (en)"),
            Arguments.of("notice", "en", "Notice (en)"),
            Arguments.of("messages.notice", "en", "Notice (en)"),
            Arguments.of("payment.headline", "en", "Payment (en)"),
            Arguments.of("payment.text", "en", "Payment Text (en)"),

            Arguments.of("headline", "de", "Headline (de)"),
            Arguments.of("messages.headline", "de", "Headline (de)"),
            Arguments.of("text", "de", "Text (de)"),
            Arguments.of("messages.text", "de", "Text (de)"),
            Arguments.of("notice", "de", "Notice (en)"),
            Arguments.of("messages.notice", "de", "Notice (en)"),
            Arguments.of("payment.headline", "de", "Payment (de)"),
            Arguments.of("payment.text", "de", "Payment Text (de)"),

            Arguments.of("headline", "en-US", "Headline (en)"),
            Arguments.of("messages.headline", "en-US", "Headline (en)"),
            Arguments.of("text", "en-US", "Text (en-US)"),
            Arguments.of("messages.text", "en-US", "Text (en-US)"),
            Arguments.of("notice", "en-US", "Notice (en)"),
            Arguments.of("messages.notice", "en-US", "Notice (en)"),
            Arguments.of("payment.headline", "en-US", "Payment (en-US)"),
            Arguments.of("payment.text", "en-US", "Payment Text (en)"),

            Arguments.of("headline", "es", "Headline (es)"),
            Arguments.of("messages.headline", "es", "Headline (es)"),
            Arguments.of("text", "es", "Text (es)"),
            Arguments.of("messages.text", "es", "Text (es)"),
            Arguments.of("notice", "es", "Notice (en)"),
            Arguments.of("messages.notice", "es", "Notice (en)"),
            Arguments.of("payment.headline", "es", "Payment (es)"),
            Arguments.of("payment.text", "es", "Payment Text (es)"),

            Arguments.of("headline", "es-CR", "Headline (es-CR)"),
            Arguments.of("messages.headline", "es-CR", "Headline (es-CR)"),
            Arguments.of("text", "es-CR", "Text (es)"),
            Arguments.of("messages.text", "es-CR", "Text (es)"),
            Arguments.of("notice", "es-CR", "Notice (en)"),
            Arguments.of("messages.notice", "es-CR", "Notice (en)"),
            Arguments.of("payment.headline", "es-CR", "Payment (es-CR)"),
            Arguments.of("payment.text", "es-CR", "Payment Text (es)"),

            Arguments.of("headline", "jp", "Headline (en)"),
            Arguments.of("messages.headline", "jp", "Headline (en)"),
            Arguments.of("payment.headline", "jp", "Payment (en)"),
            Arguments.of("payment.text", "jp", "Payment Text (en)")
        );
    }
}
