package io.github.alaugks.spring.messagesource.base.records;

import java.util.Locale;

public record Translation(String domain, Locale locale, String code, String value) {

}
