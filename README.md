> [!IMPORTANT]
> Work in progress.

# Base package to provide a custom MessageSource for Spring and Spring Boot

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=alaugks_spring-messagesource-base&metric=alert_status&token=3d2b79af1f0f0ab6089e565495b4db6f621e9a13)](https://sonarcloud.io/summary/overall?id=alaugks_spring-messagesource-base)

## Dependency

```xml
<dependencies>
    <dependency>
        <groupId>io.github.alaugks</groupId>
        <artifactId>spring-messagesource-catalog</artifactId>
        <version>0.0.9-SNAPSHOT</version>
    </dependency>
</dependencies>

<!-- Define SNAPSHOT repository  -->
<repositories>
    <repository>
        <id>ossrh-snapshot</id>
        <url>https://s01.oss.sonatype.org/content/repositories/snapshots</url>
    </repository>
</repositories>
```

## MessageSource Configuration
```java

import io.github.alaugks.spring.messagesource.catalog.catalog.CatalogHandler;
import io.github.alaugks.spring.messagesource.catalog.records.TransUnit;
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
                //.defaultDomain(String defaultDomain)
                .build()
        );
    }
}
```


### Target values

<table>
  <thead>
  <tr>
    <th>id</th>
    <th>en</th>
    <th>en-US</th>
    <th>de</th>
    <th>jp***</th>
  </tr>
  </thead>
  <tbody>
  <tr>
    <td>headline*<br>messages.headline</td>
    <td>Headline</td>
    <td>Headline**</td>
    <td>Überschrift</td>
    <td>Headline</td>
  </tr>
  <tr>
    <td>postcode*<br>messages.postcode</td>
    <td>Postcode</td>
    <td>Zip code</td>
    <td>Postleitzahl</td>
    <td>Postcode</td>
  </tr>
  <tr>
    <td>validation.email.exists*<br>messages.validation.email.exists</td>
    <td>Your email {0} has been registered.</td>
    <td>Your email {0} has been registered.**</td>
    <td>Ihre E-Mail {0} wurde registriert.</td>
    <td>Your email {0} has been registered.</td>
  </tr>
  <tr>
    <td>default-message*<br>messages.default-message</td>
    <td>This is a default message.</td>
    <td>This is a default message.**</td>
    <td>Das ist ein Standardtext.</td>
    <td>This is a default message.</td>
  </tr>
  <tr>
    <td>payment.headline</td>
    <td>Payment</td>
    <td>Payment**</td>
    <td>Zahlung</td>
    <td>Payment</td>
  </tr>
  <tr>
    <td>payment.form.expiry_date</td>
    <td>Expiry date</td>
    <td>Expiration date</td>
    <td>Ablaufdatum</td>
    <td>Expiry date</td>
  </tr>
  </tbody>
</table>

> *Default domain is `messages`.
>
> **Example of a fallback from Language_Region (`en-US`) to Language (`en`). The `id` does not exist in `en-US`, so it tries to select the translation with locale `en`.
> 
> ***There is no translation for Japanese (`jp`). The default locale transUnits (`en`) are selected.

<a name="a5"></a>

## Using the MessageSource

With the implementation and use of the MessageSource interface, the transUnits are also available in **Thymeleaf**, as **Service (Dependency Injection)** and **Custom Validation Messages**. Also in packages and implementations that use the MessageSource.

### Thymeleaf

With the configured MessageSource, the transUnits are available in Thymeleaf.

```html
<!-- Default domain: messages -->

<!-- "Headline" -->
<h1 th:text="#{headline}"/>
<h1 th:text="#{messages.headline}"/>

<!-- "Postcode" -->
<label th:text="#{postcode}"/>
<label th:text="#{messages.postcode}"/>

<!-- "Your email john.doe@example.com has been registered." -->
<span th:text="#{validation.email.exists('john.doe@example.com')}"/>
<span th:text="#{messages.validation.email.exists('john.doe@example.com')}"/>

<!-- "This is a default message." -->
<span th:text="${#messages.msgOrNull('not-exists-id')} ?: #{default-message}"/>
<span th:text="${#messages.msgOrNull('not-exists-id')} ?: #{messages.default-message}"/>


<!-- Domain: payment -->

<!-- "Payment" -->
<h2 th:text="#{payment.headline}"/>

<!-- "Expiry date" -->
<strong th:text="#{payment.form.expiry_date}"/>
```

### Service (Dependency Injection)

The MessageSource can be set via Autowire to access the transUnits.

```java
import org.springframework.context.MessageSource;

private final MessageSource messageSource;

// Autowire MessageSource
public MyClass(MessageSource messageSource) {
    this.messageSource = messageSource;
}


// Default domain: messages

// "Headline"
this.messageSource.getMessage("headline", null, locale);
this.messageSource.getMessage("messages.headline", null, locale);

// "Postcode"
this.messageSource.getMessage("postcode", null, locale);
this.messageSource.getMessage("messages.postcode", null, locale);

// "Your email john.doe@example.com has been registered."
Object[] args = {"john.doe@example.com"};
this.messageSource.getMessage("validation.email.exists", args, locale);
this.messageSource.getMessage("messages.validation.email.exists", args, locale);

// "This is a default message."
//String defaultMessage = this.messageSource.getMessage("default-message", null, locale);
String defaultMessage = this.messageSource.getMessage("messages.default-message", null, locale);
this.messageSource.getMessage("not-exists-id", null, defaultMessage, locale);


// Domain: payment

// "Payment"
this.messageSource.getMessage("payment.headline", null, locale);

// "Expiry date"
this.messageSource.getMessage("payment.expiry-date", null, locale);
```

### Custom Validation Messages

The article [Custom Validation MessageSource in Spring Boot](https://www.baeldung.com/spring-custom-validation-message-source) describes how to use custom validation messages.


## Support

If you have questions, comments or feature requests please use the [Discussions](https://github.com/alaugks/spring-xliff-translation/discussions) section.

<a name="a8"></a>


## Translation Sources

* [XLIFF 2.0.0-SNAPSHOT](https://github.com/alaugks/spring-messagesource-xliff/tree/snapshot/2.0.0)
