package io.github.alaugks.spring.messagesource.base;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alaugks.spring.messagesource.base.catalog.CatalogBuilder;
import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import io.github.alaugks.spring.messagesource.base.records.TranslationFile;
import io.github.alaugks.spring.messagesource.base.ressources.ResourcesLoader;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Properties;
import java.util.stream.Stream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.context.MessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

@SuppressWarnings({"java:S125"})
class CatalogMessageSourceBehaviourTest {

    static MessageSource messageSource;
    static ResourceBundleMessageSource resourceBundleMessageSource;

    @BeforeAll
    static void beforeAll() throws IOException {

        Map<String, Properties> messages = new HashMap<>();
        Locale defaultLocale = Locale.forLanguageTag("en");

        List<TransUnit> transUnits = new ArrayList<>();
        var resourcesLoader = new ResourcesLoader(
            Locale.forLanguageTag("en"),
            new HashSet<>(List.of("translations_example/*")),
            List.of("json")
        );

        for (TranslationFile file : resourcesLoader.getTranslationFiles()) {
            String json = new String(file.inputStream().readAllBytes(), StandardCharsets.UTF_8);
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> items = mapper.convertValue(mapper.readTree(json), new TypeReference<>() {
            });

            for (Map.Entry<String, Object> item : items.entrySet()) {
                messages.putIfAbsent(file.locale().toString(), new Properties());
                messages.get(file.locale().toString()).put(
                    file.domain() + "." + item.getKey(),
                    item.getValue().toString()
                );
                transUnits.add(
                    new TransUnit(
                        file.locale(), item.getKey(), item.getValue().toString(), file.domain()
                    )
                );
            }
        }

        for (Map.Entry<String, Properties> entry : messages.entrySet()) {
            writePropertiesFiles(entry, defaultLocale);
        }

        messageSource = new CatalogMessageSource(
            CatalogBuilder
                .builder(transUnits, defaultLocale)
                .build()
        );

        resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages/messages");
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setDefaultLocale(Locale.forLanguageTag("en"));
    }

    @ParameterizedTest()
    @MethodSource("dataProvider_examples")
    void test_baseTranslationMessageSource(String code, String locale, Object expected) {
        String message = messageSource.getMessage(
            code,
            null,
            Locale.forLanguageTag(locale)
        );
        assertEquals(expected, message);
    }

    @ParameterizedTest()
    @MethodSource("dataProvider_examples")
    void test_resourceBundleMessageSource(String code, String locale, Object expected) {
        if (!code.startsWith("messages.") && !code.startsWith("payment.")) {
            code = "messages." + code;
        }

        String message = resourceBundleMessageSource.getMessage(
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

    private static void writePropertiesFiles(Entry<String, Properties> entry, Locale defaultLocale) throws IOException {
        //entry.getValue().store(new FileOutputStream(
        //    String.format(
        //        "src/test/resources/messages/messages%s.properties",
        //        !Objects.equals(entry.getKey(), defaultLocale.toString()) ? "_" + entry.getKey() : ""
        //    )
        //), null);

        String properties = "";
        for (Entry<Object, Object> prop : entry.getValue().entrySet()) {
            properties += prop.getKey() + "=" + prop.getValue() + "\n";
        }

        FileOutputStream outputStream = new FileOutputStream(
            String.format(
                "src/test/resources/messages/messages%s.properties",
                !Objects.equals(entry.getKey(), defaultLocale.toString()) ? "_" + entry.getKey() : ""
            )
        );
        byte[] strToBytes = properties.getBytes();
        outputStream.write(strToBytes);
        outputStream.close();
    }
}
