> [!IMPORTANT]
> Work in progress.

# Base package to provide a custom MessageSource for Spring and Spring Boot

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=alaugks_spring-messagesource-base&metric=alert_status&token=3d2b79af1f0f0ab6089e565495b4db6f621e9a13)](https://sonarcloud.io/summary/overall?id=alaugks_spring-messagesource-base)

## Dependency

```xml
<dependencies>
    <dependency>
        <groupId>io.github.alaugks</groupId>
        <artifactId>spring-messagesource-base</artifactId>
        <version>0.1.0.1-SNAPSHOT</version>
    </dependency>
</dependencies>
```

## Configuration
```java
import io.github.alaugks.spring.messagesource.base.catalog.Catalog;
import io.github.alaugks.spring.messagesource.base.catalog.CatalogHandler;
import io.github.alaugks.spring.messagesource.base.records.Translation;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {
    
    @Bean
    public MessageSource messageSource() {
        
        List<Translation> translations = new ArrayList<>();
        translations.add(new Translation(Locale.forLanguageTag("en"), "hello_world", "Hello World"));
        translations.add(new Translation(Locale.forLanguageTag("de"), "hello_world", "Hallo Welt"));

        return new BaseTranslationMessageSource(
            CatalogHandler
                .builder(
                   new Catalog(
                        translations,
                        Locale.forLanguageTag("en")
                    )
                )
                .build()
        );
    }
}
```

## Translation Sources

* [XLIFF 2.0.0-SNAPSHOT](https://github.com/alaugks/spring-messagesource-xliff/tree/snapshot/2.0.0)
