// SPDX-License-Identifier: Apache-2.0
// Copyright 2024 André Laugks <alaugks@gmail.com>

package io.github.alaugks.spring.messagesource.base.record;

import io.github.alaugks.spring.messagesource.base.exception.BaseMessageSourceRuntimeException;
import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocale;
import io.github.alaugks.spring.messagesource.base.records.TransFileTargetLocaleInterface;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransFileTargetLocaleTest {

	@Test
	void test_has_no_locale() {
		TransFileTargetLocaleInterface filename = new TransFileTargetLocale(null, null);

		assertFalse(filename.hasLocale());
	}

	@Test
	void test_has_locale() {
		TransFileTargetLocaleInterface filename = new TransFileTargetLocale("en", null);

		assertTrue(filename.hasLocale());
	}

	@Test
	void test_invalid_locale_exception() {
		TransFileTargetLocaleInterface filename = new TransFileTargetLocale("en", "bar");

		assertThrows(BaseMessageSourceRuntimeException.class, filename::locale);
	}
}
