// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base;

import java.util.Locale;

@FunctionalInterface
interface MessageFormatter {
	String format(String value, Locale locale, Object[] args);
}
