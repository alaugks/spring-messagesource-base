package io.github.alaugks.spring.messagesource.base.records;

import java.util.Locale;
import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;

public interface TransFileInterface {

	/**
	 * Retrieves the locale associated with this instance.
	 *
	 * @return the {@link Locale} object representing the locale; never null
	 */
	Locale locale();

	/**
	 * Retrieves the raw content of the file as a byte array.
	 *
	 * @return a byte array containing the raw file content; may be null if no content is associated
	 */
	byte @Nullable [] content();

	/**
	 * Retrieves the associated resource of the translation file.
	 *
	 * @return the {@link Resource} representing the file; may be null if no resource is associated.
	 */
	@Nullable Resource resource();
}
