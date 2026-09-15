// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.records;

import io.github.alaugks.spring.messagesource.base.exception.BaseMessageSourceRuntimeException;
import java.util.IllformedLocaleException;
import java.util.Locale;
import org.jspecify.annotations.Nullable;

/**
 * Represents a parsed filename, potentially consisting of language and region parts.
 * This class is intended to interpret localization information encoded within file names.
 *
 * @param language the optional language component extracted from the file name; may be {@code null}
 * @param region   the optional region component extracted from the file name; may be {@code null}
 */
public record TransFileTargetLocale(@Nullable String language, @Nullable String region) implements
	TransFileTargetLocaleInterface {

	/**
	 * Constructs an instance of {@code TransFileTargetLocale} using the given {@link Locale}.
	 *
	 * This constructor extracts the language and region components from the provided locale
	 * and uses them to initialize the corresponding fields of the record.
	 *
	 * @param locale the {@link Locale} object from which the language and region components
	 *               are derived; must not be {@code null}.
	 */
	public TransFileTargetLocale(Locale locale) {
		this(locale.getLanguage(), locale.getCountry());
	}

	/**
	 * {@return {@code true} when the file name contains a language part}
	 */
	@Override
	public boolean hasLocale() {
		return this.locale() != null;
	}

	/**
	 * {@return the locale built from the language and region parts, or {@code null} when no
	 * language is present}
	 * @throws BaseMessageSourceRuntimeException if the parsed parts do not form a valid locale
	 */
	@Override
	public @Nullable Locale locale() {
		try {
			if (this.language != null) {
				Locale.Builder localeBuilder = new Locale.Builder();
				localeBuilder.setLanguage(this.language);
				if (this.region != null) {
					localeBuilder.setRegion(this.region);
				}
				return localeBuilder.build();
			}
			return null;
		}
		catch (IllformedLocaleException e) {
			throw new BaseMessageSourceRuntimeException(e);
		}
	}
}
