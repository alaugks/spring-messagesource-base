package io.github.alaugks.spring.messagesource.base.records;

import java.io.InputStream;
import java.util.Locale;

public record TranslationFile(String domain, Locale locale, InputStream inputStream) {

}
