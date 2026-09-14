// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base;

import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.springframework.context.MessageSource;
import org.springframework.util.Assert;

/**
 * Base class for fluent builders that share the common catalog configuration: the configured
 * sources, default locale, default domain, ICU4J formatting, and an optional parent message
 * source.
 *
 * <p>The recursive type parameter {@code B} lets each concrete builder return its own type from
 * the shared fluent methods, so chaining stays type-safe across subclasses (including sibling
 * builders in other packages).
 *
 * @param <B> the concrete builder type returned by the fluent methods
 */
public abstract class AbstractBaseMessageSourceBuilder<B extends AbstractBaseMessageSourceBuilder<B>> {

    /** Locale used as fallback when a code cannot be resolved for the requested locale. */
    private final Locale defaultLocale;

    /** Whether messages are formatted with ICU4J. */
    private boolean useICU4j = false;

    /** Optional parent consulted when a code cannot be resolved locally. */
    private @Nullable MessageSource parentMessageSource = null;

    protected AbstractBaseMessageSourceBuilder(Locale defaultLocale) {
        Assert.notNull(defaultLocale, "Argument defaultLocale must not be null");

        this.defaultLocale = defaultLocale;
    }

    /**
     * {@return the default locale used as a fallback when a code cannot be resolved for the
     * requested locale}
     */
    protected Locale getDefaultLocale() {
        return this.defaultLocale;
    }

    /**
     * {@return {@code true} if ICU4J formatting is enabled}
     * @see #enableICU4j()
     */
    protected boolean isICU4jEnabled() {
        return this.useICU4j;
    }

    /**
     * Configures whether ICU4J message formatting is enabled.
     *
     * @param useICU4j {@code true} to enable ICU4J message formatting,
     *                 {@code false} to use standard Java message formatting
     * @return this builder
     */
    public B useICU4j(boolean useICU4j) {
        this.useICU4j = useICU4j;

        return (B) this;
    }

    /**
     * Enables ICU4J message formatting. When enabled, resolved messages are formatted with
     * {@link com.ibm.icu.text.MessageFormat} (supporting ICU syntax such as named arguments
     * and {@code plural}/{@code select}); otherwise {@link java.text.MessageFormat} is used.
     *
     * @return this builder
     */
    public B enableICU4j() {
        this.useICU4j = true;

        return (B) this;
    }

    /**
     * Sets the parent message source to be used as a fallback when a message cannot be resolved
     * from the current sources or domains.
     *
     * @param messageSource the parent message source; may be {@code null} for no parent
     * @return this builder
     */
    public B parentMessageSource(@Nullable MessageSource messageSource) {
        this.parentMessageSource = messageSource;

        return (B) this;
    }

    /**
     * {@return the parent message source used as a fallback, or {@code null} if none is configured}
     * @see #parentMessageSource(MessageSource)
     */
    protected @Nullable MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }
}
