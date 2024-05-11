package io.github.alaugks.spring.messagesource.base;

import io.github.alaugks.spring.messagesource.base.catalog.CatalogHandlerInterface;
import java.text.MessageFormat;
import java.util.Locale;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

public class BaseTranslationMessageSource implements MessageSource {

    private final CatalogHandlerInterface catalogHandler;

    public BaseTranslationMessageSource(CatalogHandlerInterface catalogHandler) {
        Assert.notNull(catalogHandler, "catalogHandler must not be null");

        this.catalogHandler = catalogHandler;
    }

    @Nullable
    public String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        return this.format(
            this.internalMessageWithDefaultMessage(code, defaultMessage, locale),
            args,
            locale
        );
    }

    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException {
        String message = this.internalMessage(code, locale);
        if (message != null) {
            return this.format(message, args, locale);
        }

        throw new NoSuchMessageException(code, locale);
    }

    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        String[] codes = resolvable.getCodes();
        if (codes != null) {
            for (String code : codes) {
                String message = this.internalMessage(code, locale);
                if (message != null) {
                    return this.format(message, resolvable.getArguments(), locale);
                }
            }
        }
        if (resolvable instanceof DefaultMessageSourceResolvable) {
            String defaultMessage = resolvable.getDefaultMessage();
            if (defaultMessage != null) {
                return this.format(defaultMessage, resolvable.getArguments(), locale);
            }
        }

        throw new NoSuchMessageException(codes != null && codes.length > 0 ? codes[codes.length - 1] : "", locale);
    }

    private String internalMessage(String code, Locale locale) throws NoSuchMessageException {
        return this.findInCatalog(locale, code);
    }

    private String internalMessageWithDefaultMessage(String code, @Nullable String defaultMessage, Locale locale) {
        String translation = this.findInCatalog(locale, code);
        if (translation != null) {
            return translation;
        }
        return defaultMessage;
    }

    private String findInCatalog(Locale locale, String code) {
        return this.catalogHandler.get(locale, code);
    }

    private String format(String message, @Nullable Object[] args, Locale locale) {
        if (args != null && args.length > 0) {
            return new MessageFormat(message, locale).format(args);
        }
        return message;
    }
}
