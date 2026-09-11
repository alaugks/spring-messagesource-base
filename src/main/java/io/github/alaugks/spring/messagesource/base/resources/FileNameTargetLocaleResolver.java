// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.resources;

import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocale;
import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocaleInterface;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;

/**
 * Resolves the target locale of a translation resource from its file name into a
 * {@link TransFileTargetLocale} record.
 *
 * <p>Matches case-insensitively. A file name must carry a leading, non-empty segment before the
 * locale part and the extension; the locale part may be separated by {@code _}, {@code -} or
 * {@code .}, and language and region may be separated by {@code _} or {@code -}.
 *
 * <p>Examples (any of the separator combinations above works the same way):
 * <ul>
 *   <li>{@code messages.ext} &rarr; no locale part</li>
 *   <li>{@code messages_de.ext} &rarr; language={@code de}</li>
 *   <li>{@code messages-de.ext} &rarr; language={@code de}</li>
 *   <li>{@code messages.de.ext} &rarr; language={@code de}</li>
 *   <li>{@code messages_en_US.ext} &rarr; language={@code en}, region={@code US}</li>
 *   <li>{@code messages_en-US.ext} &rarr; language={@code en}, region={@code US}</li>
 *   <li>{@code messages-en_US.ext} &rarr; language={@code en}, region={@code US}</li>
 *   <li>{@code messages-en-US.ext} &rarr; language={@code en}, region={@code US}</li>
 *   <li>{@code messages.en_US.ext} &rarr; language={@code en}, region={@code US}</li>
 *   <li>{@code messages.en-US.ext} &rarr; language={@code en}, region={@code US}</li>
 * </ul>
 */
public class FileNameTargetLocaleResolver implements TargetLocaleResolverInterface {

	/** Matches the optional language/region suffix and the mandatory extension at the end of a resource file name. */
	private static final Pattern PATTERN = Pattern.compile(
		"(?:[_.-](?<language>[a-z]+)(?:[_-](?<region>[a-z]+))?)?\\.[a-z0-9]+$",
		Pattern.CASE_INSENSITIVE
	);

	@Override
	public @Nullable TransFileTargetLocaleInterface resolve(Resource resource) {
		String filename = resource.getFilename();
		Assert.notNull(filename, "Argument filename must not be null");

		Matcher matcher = PATTERN.matcher(filename);

		if (matcher.find() && matcher.start() > 0) {
			return new TransFileTargetLocale(
				matcher.group("language"),
				matcher.group("region")
			);
		}

		return null;
	}
}
