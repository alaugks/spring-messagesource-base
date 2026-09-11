// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.records;

import java.util.Locale;

/**
 *
 * @param locale
 * @param code
 * @param value
 */
public record TransUnit(Locale locale, String code, String value) implements TransUnitInterface {

}
