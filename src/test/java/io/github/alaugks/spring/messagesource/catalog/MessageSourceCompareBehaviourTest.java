package io.github.alaugks.spring.messagesource.catalog;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.alaugks.spring.messagesource.catalog.catalog.CatalogBuilder;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
import io.github.alaugks.spring.messagesource.catalog.records.TranslationFile;
import io.github.alaugks.spring.messagesource.catalog.ressources.ResourcesLoader;
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
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.context.support.ResourceBundleMessageSource;

/**
 * This test compares the logic when resolving the code of CatalogMessageSource vs. ResourceBundleMessageSource
 * and ReloadableResourceBundleMessageSource. Behaviour must be equal.
 */
@SuppressWarnings({"java:S125"})
class MessageSourceCompareBehaviourTest {

    static MessageSource catalogMessageSource;
    static ResourceBundleMessageSource resourceBundleMessageSource;
    static ReloadableResourceBundleMessageSource reloadableResourceBundleMessageSource;

    @BeforeAll
    static void beforeAll() throws IOException {
        Locale defaultLocale = Locale.forLanguageTag("en");

        catalogMessageSource = new CatalogMessageSource(
            CatalogBuilder
                .builder(loadTransUnits(defaultLocale), defaultLocale)
                .build()
        );

        resourceBundleMessageSource = new ResourceBundleMessageSource();
        resourceBundleMessageSource.setBasename("messages/messages");
        resourceBundleMessageSource.setDefaultEncoding("UTF-8");
        resourceBundleMessageSource.setDefaultLocale(Locale.forLanguageTag("en"));

        reloadableResourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
        reloadableResourceBundleMessageSource.setBasename("messages/messages");
        reloadableResourceBundleMessageSource.setDefaultEncoding("UTF-8");
        reloadableResourceBundleMessageSource.setDefaultLocale(Locale.forLanguageTag("en"));
    }

    @ParameterizedTest()
    @MethodSource("dataProvider_examples")
    void test_baseTranslationMessageSource(String locale, String code, Object[] args, Object expected) {
        assertEquals(expected, catalogMessageSource.getMessage(
            code,
            args,
            Locale.forLanguageTag(locale)
        ));
    }

    @ParameterizedTest()
    @MethodSource("dataProvider_examples")
    void test_resourceBundleMessageSource(String locale, String code, Object[] args, Object expected) {
        if (!code.startsWith("messages.") && !code.startsWith("payment.")) {
            code = "messages." + code;
        }

        assertEquals(expected, resourceBundleMessageSource.getMessage(
            code,
            args,
            Locale.forLanguageTag(locale)
        ));
    }

    @ParameterizedTest()
    @MethodSource("dataProvider_examples")
    void test_reloadableResourceBundleMessageSource(String locale, String code, Object[] args, Object expected) {
        if (!code.startsWith("messages.") && !code.startsWith("payment.")) {
            code = "messages." + code;
        }

        assertEquals(expected, reloadableResourceBundleMessageSource.getMessage(
            code,
            args,
            Locale.forLanguageTag(locale)
        ));
    }

    private static Stream<Arguments> dataProvider_examples() {
        return Stream.of(
            Arguments.of("en", "headline", null, "Headline (en)"),
            Arguments.of("en", "messages.headline", null, "Headline (en)"),
            Arguments.of("en", "text", null, "Text (en)"),
            Arguments.of("en", "messages.text", null, "Text (en)"),
            Arguments.of("en", "notice", null, "Notice (en)"),
            Arguments.of("en", "messages.notice", null, "Notice (en)"),
            Arguments.of("en", "payment.headline", null, "Payment (en)"),
            Arguments.of("en", "payment.text", null, "Payment Text (en)"),
            Arguments.of("en", "messageformat", new Object[]{10000}, "There are 10,000 files."),

            Arguments.of("de", "headline", null, "Headline (de)"),
            Arguments.of("de", "messages.headline", null, "Headline (de)"),
            Arguments.of("de", "text", null, "Text (de)"),
            Arguments.of("de", "messages.text", null, "Text (de)"),
            Arguments.of("de", "notice", null, "Notice (en)"),
            Arguments.of("de", "messages.notice", null, "Notice (en)"),
            Arguments.of("de", "payment.headline", null, "Payment (de)"),
            Arguments.of("de", "payment.text", null, "Payment Text (de)"),
            Arguments.of("de", "messageformat", new Object[]{10000}, "Es gibt 10.000 Dateien."),

            Arguments.of("en-US", "headline", null, "Headline (en)"),
            Arguments.of("en-US", "messages.headline", null, "Headline (en)"),
            Arguments.of("en-US", "text", null, "Text (en-US)"),
            Arguments.of("en-US", "messages.text", null, "Text (en-US)"),
            Arguments.of("en-US", "notice", null, "Notice (en)"),
            Arguments.of("en-US", "messages.notice", null, "Notice (en)"),
            Arguments.of("en-US", "payment.headline", null, "Payment (en-US)"),
            Arguments.of("en-US", "payment.text", null, "Payment Text (en)"),

            Arguments.of("es", "headline", null, "Headline (es)"),
            Arguments.of("es", "messages.headline", null, "Headline (es)"),
            Arguments.of("es", "text", null, "Text (es)"),
            Arguments.of("es", "messages.text", null, "Text (es)"),
            Arguments.of("es", "notice", null, "Notice (en)"),
            Arguments.of("es", "messages.notice", null, "Notice (en)"),
            Arguments.of("es", "payment.headline", null, "Payment (es)"),
            Arguments.of("es", "payment.text", null, "Payment Text (es)"),

            Arguments.of("es-CR", "headline", null, "Headline (es-CR)"),
            Arguments.of("es-CR", "messages.headline", null, "Headline (es-CR)"),
            Arguments.of("es-CR", "text", null, "Text (es)"),
            Arguments.of("es-CR", "messages.text", null, "Text (es)"),
            Arguments.of("es-CR", "notice", null, "Notice (en)"),
            Arguments.of("es-CR", "messages.notice", null, "Notice (en)"),
            Arguments.of("es-CR", "payment.headline", null, "Payment (es-CR)"),
            Arguments.of("es-CR", "payment.text", null, "Payment Text (es)"),

            Arguments.of("jp", "headline", null, "Headline (en)"),
            Arguments.of("jp", "messages.headline", null, "Headline (en)"),
            Arguments.of("jp", "payment.headline", null, "Payment (en)"),
            Arguments.of("jp", "payment.text", null, "Payment Text (en)"),
            Arguments.of("jp", "messageformat", new Object[]{10000}, "There are 10,000 files.")
        );
    }

    private static List<TransUnit> loadTransUnits(Locale defaultLocale) throws IOException {
        Map<String, Properties> messages = new HashMap<>();

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

        return transUnits;
    }

    private static void writePropertiesFiles(Entry<String, Properties> entry, Locale defaultLocale) throws IOException {
        //entry.getValue().store(new FileOutputStream(
        //    String.format(
        //        "src/test/resources/messages/messages%s.properties",
        //        !Objects.equals(entry.getKey(), defaultLocale.toString()) ? "_" + entry.getKey() : ""
        //    )
        //), null);

        StringBuilder properties = new StringBuilder();
        for (Entry<Object, Object> prop : entry.getValue().entrySet()) {
            properties.append(prop.getKey()).append("=").append(prop.getValue()).append("\n");
        }

        FileOutputStream outputStream = new FileOutputStream(
            String.format(
                "src/test/resources/messages/messages%s.properties",
                !Objects.equals(entry.getKey(), defaultLocale.toString()) ? "_" + entry.getKey() : ""
            )
        );
        byte[] stream = properties.toString().getBytes();
        outputStream.write(stream);
        outputStream.close();
    }
}
