// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.records;

import java.util.Locale;
import org.jspecify.annotations.Nullable;

public interface TransFileTargetLocaleInterface {

	/**
	 * {@return {@code true} when the file name contains a language part}
	 */
	boolean hasLocale();

	/**
	 * {@return the locale built from the language and region parts, or {@code null} when no
	 * language is present}
	 */
	@Nullable Locale locale();

	/**
	 * {@return the language component extracted from the file name, or {@code null} if none is present}
	 */
	@Nullable String language();

	/**
	 * {@return the region component extracted from the file name, or {@code null} if none is present}
	 */
	@Nullable String region();
}
