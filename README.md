# Base functionality to create a custom Spring MessageSource

This package provides the [MessageSource interface](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/MessageSource.html). Internally, it builds a [`ResourceBundle`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/util/ResourceBundle.html) and delegates locale fallback handling to it — the same mechanism Spring's own [ResourceBundleMessageSource](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/support/ResourceBundleMessageSource.html) relies on.

[![Quality gate status](https://sonarcloud.io/api/project_badges/measure?project=alaugks_spring-messagesource-base&metric=alert_status&token=3f69e5749285dea5ae7ba2f6696d1fb976e7f51d)](https://sonarcloud.io/summary/new_code?id=alaugks_spring-messagesource-base)
[![Maven Central](https://img.shields.io/maven-central/v/io.github.alaugks/spring-messagesource-base.svg?label=Maven%20Central)](https://central.sonatype.com/artifact/io.github.alaugks/spring-messagesource-base/0.2.0)

## Table of Contents

- [Dependency](#dependency)
  - [Maven](#maven)
  - [Gradle](#gradle)
- [Packages that use this package as a base package](#packages-that-use-this-package-as-a-base-package)
- [BaseMessageSource Configuration](#basemessagesource-configuration)
  - [Options](#options)
  - [TransUnit Record](#transunit-record)
  - [Configuration example](#configuration-example)
- [Message formatting](#message-formatting)
  - [Default (java.text.MessageFormat)](#default-javatextmessageformat)
  - [ICU4J (com.ibm.icu.text.MessageFormat)](#icu4j-comibmicutextmessageformat)
    - [Plural](#plural)
    - [Select (and gender)](#select-and-gender)
- [Interfaces](#interfaces)
- [License](#license)

## Dependency

### Maven

```xml
<dependency>
    <groupId>io.github.alaugks</groupId>
    <artifactId>spring-messagesource-base</artifactId>
    <version>0.2.0</version>
</dependency>
```

### Gradle

```
implementation group: 'io.github.alaugks', name: 'spring-messagesource-base', version: '0.2.0'
```

## Packages that use this package as a base package

* [spring-messagesource-xliff](https://github.com/alaugks/spring-messagesource-xliff): Xliff MessageSource for Spring
* [spring-messagesource-json](https://github.com/alaugks/spring-messagesource-json): JSON MessageSource for Spring
* [spring-messagesource-db-example](https://github.com/alaugks/spring-messagesource-db-example): Example custom Spring MessageSource from database

## BaseMessageSource Configuration

### Options

| Method                                                               | Default    | Description                                                                                                                                                                                                                                                  |
|----------------------------------------------------------------------|------------|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `builder(Locale defaultLocale, List<TransUnitInterface> transUnits)` | —          | Entry point.<br><br>`defaultLocale` is the locale to fall back to when a code cannot be resolved for the requested locale.<br><br>`transUnits` are aggregated into the in-memory base.                                                                       |
| `enableICU4j()`                                                      | disabled   | Format messages with ICU4J's `com.ibm.icu.text.MessageFormat` instead of the default `java.text.MessageFormat`. Adds named arguments and ICU `plural`/`select` patterns. See [Message formatting](#message-formatting) for details and examples.             |
| `parentMessageSource(MessageSource parentMessageSource)`             | —          | Sets a parent [`MessageSource`](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/MessageSource.html) to delegate to. When a code cannot be resolved in the base, the lookup falls back to the parent source.     |
| `build()`                                                            | —          | Builds the `BaseMessageSourceBuilder` from the configured trans units and default locale. Trans units are aggregated at this point; subsequent mutations of the builder have no effect on the returned instance.                                          |

### TransUnit Record

```java
TransUnit(Locale locale, String code, String value);
```


### Configuration example

#### MessageConfig with List of TransUnits 

```java
import io.github.alaugks.spring.messagesource.base.BaseMessageSourceBuilder;
import io.github.alaugks.spring.messagesource.base.records.TransUnit;
import io.github.alaugks.spring.messagesource.base.records.TransUnitInterface;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MessageConfig {
    
    private final List<TransUnitInterface> transUnits = new ArrayList<>() {{
        // en
        add(new TransUnit(Locale.forLanguageTag("en"), "headline", "Headline"));
        add(new TransUnit(Locale.forLanguageTag("en"), "postcode", "Postcode"));

        // en-US
        add(new TransUnit(Locale.forLanguageTag("en-US"), "postcode", "Zip code"));

        // de
        add(new TransUnit(Locale.forLanguageTag("de"), "headline", "Überschrift"));
        add(new TransUnit(Locale.forLanguageTag("de"), "postcode", "Postleitzahl"));
    }};

    @Bean
    public MessageSource messageSource() {
        return BaseMessageSourceBuilder
            .builder(Locale.forLanguageTag("en"), this.transUnits)
            .build();
    }
}
```

#### Target values

Resolving the target value based on the code behaves like the `ResourceBundleMessageSource` or `ReloadableResourceBundleMessageSource`.

<table>
  <thead>
  <tr>
    <th>code</th>
    <th>en</th>
    <th>en-US</th>
    <th>de</th>
    <th>jp**</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>headline</td>
    <td>Headline</td>
    <td>Headline*</td>
    <td>Überschrift</td>
    <td>Headline</td>
  </tr>
  <tr>
    <td>postcode</td>
    <td>Postcode</td>
    <td>Zip code</td>
    <td>Postleitzahl</td>
    <td>Postcode</td>
  </tr>
  </tbody>
</table>

> *Example of a fallback from Language_Region (`en-US`) to Language (`en`). The `id` does not exist in `en-US`, so it tries to select the translation with locale `en`.
>
> **There is no translation for Japanese (`jp`). The default locale transUnits (`en`) are selected.

## Message formatting

A resolved value is formatted before it is returned. The arguments passed to `getMessage(...)` are applied
to the message pattern. Two formatters are available: the default `java.text.MessageFormat`, and
`com.ibm.icu.text.MessageFormat` once `enableICU4j()` is set.

> [!IMPORTANT]
> Named arguments and ICU `plural`/`select` patterns (e.g. `{count, plural, …}`) cannot be resolved by the default `java.text.MessageFormat`. They fail at `getMessage()` time. To use them you **must** enable ICU4J via `enableICU4j()`.
>
> ICU4J is the [`com.ibm.icu:icu4j`](https://central.sonatype.com/artifact/com.ibm.icu/icu4j) dependency. It is shipped transitively with this package; no extra dependency is required. Its `com.ibm.icu.text.MessageFormat` is a syntax superset of `java.text.MessageFormat`. Existing numeric-index patterns keep working.
>
> The two are not fully output-compatible. ICU4J uses Unicode CLDR locale data, so the formatted result for a given locale can differ from the JDK's. One example is the decimal and grouping separators in numbers (`.` vs `,`). Verify locale-sensitive output after enabling ICU4J.

### Default (java.text.MessageFormat)

Without `enableICU4j()`, values are formatted with [`java.text.MessageFormat`](https://docs.oracle.com/en/java/javase/17/docs/api/java.base/java/text/MessageFormat.html).
This is the same formatter Spring's `ResourceBundleMessageSource` uses. It only understands **numeric
argument indices** (`{0}`, `{1}`, …), passed positionally as an `Object[]`. Numbers are formatted
locale-aware; grouping separators differ per locale.

```java
new TransUnit(Locale.forLanguageTag("en"), "files", "There are {0,number,integer} files.");
new TransUnit(Locale.forLanguageTag("de"), "files", "Es gibt {0,number,integer} Dateien.");

messageSource.getMessage(
    "files",
    new Object[] { 10000 },
    Locale.forLanguageTag("de")
);
```

**Result:** `Es gibt 10.000 Dateien.`

#### Plural (`choice` format)

`java.text.MessageFormat` has no `plural` keyword. Its `choice` sub-format covers the common plural case
by mapping **numeric ranges** to variants. A limit followed by `#` matches values from that number up;
`<` matches values strictly greater. Each `|`-separated case is itself a pattern. To insert the number,
reference it again as `{0,number,integer}`.

```java
new TransUnit(
    Locale.forLanguageTag("en"),
    "file_deleted",
    "{0,choice,0#You deleted no files.|1#You deleted one file.|1<You deleted {0,number,integer} files.}"
);
new TransUnit(
    Locale.forLanguageTag("de"),
    "file_deleted",
    "{0,choice,0#Sie haben keine Dateien gelöscht.|1#Sie haben eine Datei gelöscht.|1<Sie haben {0,number,integer} Dateien gelöscht.}"
);

messageSource.getMessage(
    "file_deleted",
    new Object[] { 1000 },
    Locale.forLanguageTag("de")
);
```

**Result:** `Sie haben 1.000 Dateien gelöscht.`

#### Select

`java.text.MessageFormat` has **no** `select` construct. It can only branch on numbers via `choice`, not
on arbitrary string values. Value-based choices such as grammatical gender cannot be expressed. For
string-based `select` (and CLDR plural categories like `few`/`many`), enable
[ICU4J](#icu4j-comibmicutextmessageformat).

### ICU4J (com.ibm.icu.text.MessageFormat)

Enable ICU4J on the builder to format with [ICU4J's `MessageFormat`](https://unicode-org.github.io/icu-docs/apidoc/released/icu4j/com/ibm/icu/text/MessageFormat.html):

```java
@Bean
public MessageSource messageSource() {
    return BaseMessageSourceBuilder
        .builder(Locale.forLanguageTag("en"), this.transUnits)
        .enableICU4j() // required for named arguments and plural/select
        .build();
}
```

With ICU4J enabled, patterns can use **named arguments** and the ICU `plural`/`select` constructs. Named
arguments are passed as a single `Map`, not as positional `{0}` / `{1}` arguments. The builder detects a
lone `Map` argument and formats the pattern with it.

#### Plural

A `plural` switch selects a variant based on a number. Each case is either an **exact number**, matched as
`=N`, or a **CLDR plural keyword** (`zero`, `one`, `two`, `few`, `many`, `other`). The locale's plural
rules map the number to one of these keywords. The number itself is inserted into a case by referencing
the argument name, `{count}`.

Which keywords a language uses, and how each number maps to one, is defined per language in the
[Unicode CLDR Language Plural Rules](https://www.unicode.org/cldr/charts/latest/supplemental/language_plural_rules.html).

```java
new TransUnit(
    Locale.forLanguageTag("en"),
    "file_deleted",
    "{count, plural, =0 {You deleted no files.} =1 {You deleted one file.} other {You deleted {count} files.}}"
);
new TransUnit(
    Locale.forLanguageTag("de"),
    "file_deleted",
    "{count, plural, =0 {Sie haben keine Dateien gelöscht.} =1 {Sie haben eine Datei gelöscht.} other {Sie haben {count} Dateien gelöscht.}}"
);

messageSource.getMessage(
    "file_deleted",
    new Object[] { Map.of("count", 1000) },
    Locale.forLanguageTag("de")
);
```

**Result:** `Sie haben 1.000 Dateien gelöscht.`

#### Select (and gender)

A `select` switch picks the case whose value matches the argument. Use it for any value-based choice such
as grammatical gender. A final `other` case acts as the fallback.

```java
new TransUnit(
    Locale.forLanguageTag("en"),
    "greeting",
    "{recipient_gender, select, feminine {How is she?} masculine {How is he?} other {How are they?}}"
);
new TransUnit(
    Locale.forLanguageTag("de"),
    "greeting",
    "{recipient_gender, select, feminine {Wie geht es ihr?} masculine {Wie geht es ihm?} other {Wie geht es ihnen?}}"
);

messageSource.getMessage(
    "greeting",
    new Object[] { Map.of("recipient_gender", "feminine") },
    Locale.forLanguageTag("de")
);
```

**Result:** `Wie geht es ihr?`

## License

Licensed under the [Apache License, Version 2.0](LICENSE).

