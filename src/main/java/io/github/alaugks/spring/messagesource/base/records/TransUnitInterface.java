// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.records;

import java.util.Locale;

public interface TransUnitInterface {

	/**
	 * {@return the locale this trans unit belongs to}
	 */
	Locale locale();

	/**
	 * {@return the message code}
	 */
	String code();

	/**
	 * {@return the translated value for the code}
	 */
	String value();
}
