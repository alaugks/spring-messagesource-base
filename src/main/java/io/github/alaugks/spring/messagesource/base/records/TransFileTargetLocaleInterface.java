package io.github.alaugks.spring.messagesource.base.records;

import java.util.Locale;
import org.jspecify.annotations.Nullable;

public interface TransFileTargetLocaleInterface {

	boolean hasLocale();

	@Nullable Locale locale();

	String language();

	String region();
}
