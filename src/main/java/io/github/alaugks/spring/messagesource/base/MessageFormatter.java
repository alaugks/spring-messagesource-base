package io.github.alaugks.spring.messagesource.base;

import java.util.Locale;

@FunctionalInterface
interface MessageFormatter {
	String format(String value, Locale locale, Object[] args);
}
