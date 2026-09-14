// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.records;

import java.util.Locale;

/**
 * A single translated message: the locale it belongs to, its code, and its value.
 *
 * @param locale the locale this trans unit belongs to
 * @param code   the message code
 * @param value  the translated value for the code
 */
public record TransUnit(Locale locale, String code, String value) implements TransUnitInterface {

}
